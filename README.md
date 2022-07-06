# APOD-COMPOSE
This project is a little demo about the usage of jetpack compose and redux kotlin.

## The APOD api
The APOD api is provided by nasa and returns the Astronomic Picture of the Day.

It is used in the homepage with the "startDate" parameter to retrieve the last N apods, and with the
"date" parameter in the detail to retrieve the apod of that day.

## Redux/Flux
The Flux design pattern is composed by four parts organized in a one-way data pipeline: Action, Dispatcher, Store and View.

The view dispatches an action to the store, the store receives it and handles it using its sub-components, then notifies the View if something changed in the data source.

Inside the store, there's two components: the State, representing the state of the whole application, and the Reducers, whose job is to take the payload of an action and copy it in a new instance of the state.

To handle asyncronous actions (like an http download), the action is intercepted by a component that I called Tales (inspired from [Angular's sagas](https://dev.to/jonrimmer/ngrx-sagas-39a8) and [ReduxObservables' epics](https://redux-observable.js.org/docs/basics/Epics.html)) that starts a coroutine that returns a new action to be dispatched.

### The app state
The app is composed by two sections: the home and the detail.

The same applies to the appstate. In addition, a DepsState contains the dependencies needed by the tales, [in a similar way as how redux-observables handles it](https://redux-observable.js.org/docs/recipes/InjectingDependenciesIntoEpics.html):
```kotlin
data class AppState(
    val deps: DepsState = DepsState(),
    val home: HomeState = HomeState(),
    val detail: DetailState = DetailState(),
)
```
### The actions
The action base class is just
```kotlin
open class Action
```
In a multiplatform project that contains a js target you may want to assign the base class a unique id to avoid checking the action class type with too many instanceof

We simply then have the Home Actions as follows:
```kotlin
open class HomeActions : Action() {
    // Initialize the home page
    class Init : HomeActions()
    //Called when the homepage data is ready
    data class DataRetrieved(val data: List<ApodModel>) : HomeActions()
}
```
### The reducers
To avoid looking for bugs everywhere, all the logic should be places into the Tales and the reducer should be quite simple and only create a new state to avoid having to write unit tests for them.

For instance, the homeReducer just copy the data to the state when it's ready:
```kotlin
fun homeReducer(state: HomeState, action: Any): HomeState {
    return when (action) {
        is HomeActions.DataRetrieved -> state.copy(apods = action.data)
        else -> state
    }
}
```
### The tales
A tale is a suspend method that takes an action and the state as an input, and returns a list of actions to be dispatched after its execution.

For instance, the homeTales, when it intercepts the HomeActions.Init action:
> * Takes the needed dependencies from the DepsState
> * Execute the http request using the ktor library
> * Returns a new action with the request response as the payload
```kotlin
internal suspend fun homeTales(action: Action, state: AppState): List<Action> {
    return when (action) {
        is HomeActions.Init -> {
            val (_, httpClient, getDate) = state.deps
            handleInit(httpClient, getDate)
        }
        else -> listOf()
    }
}

private suspend fun handleInit(
    httpClient: HttpClient,
    getDate: (deltaDays: Int) -> LocalDate
): List<Action> {
    val date = getDate(-9)
    val url: String =
        "https://api.nasa.gov/planetary/apod?" +
                "api_key=MY_API_KEY" +
                "&thumbs=true&" +
                "start_date=${date.year}-${date.month.number}-${date.dayOfMonth}"
    val body: String = httpClient.get(url).body()
    val list =
        Gson().fromJson(body, Array<ApodModel>::class.java).toList().sortedByDescending { it.date }
    return listOf(
        HomeActions.DataRetrieved(list)
    )
}
```
### Initializing the store
We are now ready to initialize the store with a custom middleware. A middleware is a function that takes the store, the action and a "next" method to be called to dispatch the action to the reducer.

We handle it this way:

```kotlin
private fun buildMiddleware() = middleware<AppState> { store, next, action ->
    // pass the action to the reducers
    next(action)
    
    // notify the subscriber that the action has been handled by the reducer
    subscriptions.forEach {
        MainScope().launch {
            it.second(action as Action)
        }
    }
    
    // foreach tale
    tales.forEach {
        MainScope().launch {
            try {
                // launch the coroutine and dispatch the actions in the returned list
                it(action as Action, store.state).forEach { store.dispatch(it) }
            } catch (e: Exception) {
                store.state.deps.log(e.toString())
            }
        }
    }
}
```
After that, we just need to initialize the store passing the root reducer, the tales list and the middleware:
```kotlin
// Root reducer
private fun reducer(state: AppState, action: Any) = AppState(
    deps = DepsState(),
    home = homeReducer(state.home, action),
    detail = detailReducer(state.detail, action),
)

//Tales list
private val tales: MutableList<suspend (action: Action, state: AppState) -> List<Action>> =
    mutableListOf(
        ::homeTales,
        ::detailTales
    )
    
//Store initialization
var store = createThreadSafeStore(
    ::reducer,
    AppState(),
    applyMiddleware(buildMiddleware())
)
```
## Jetpack Compose
Jetpack Compose is Android’s modern toolkit for building native UI. It simplifies and accelerates UI development on Android. Quickly bring your app to life with less code, powerful tools, and intuitive Kotlin APIs.

For example, this is the composable for the full-width image in the apod detail:
```kotlin
@Composable
fun DetailImage(url: String) {
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth(),
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .crossfade(500)
            .build(),
        contentDescription = "apod detail image",
        contentScale = ContentScale.FillWidth,
    )
}
```
An apod could also contain an url of a youtube video. For this reason instead of an image we use a webview to show the youtube webpage. This is a nice example on how to integrate legacy views inside of a composable:
```kotlin
@Composable
fun Webview(url: String) {
    val context = LocalContext.current
    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16.0f / 9), factory = {
        WebView(context).apply {
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    })
}
```
As you can see, this syntax is very clear and one can visualize the composable by reading the code way easier than with XML or other non-dsl programmatical view creation! In addition, in the latest android studio build, you can also see the composable preview in the IDE without building the whole application.

### Navigation
Navigation is handled using a NavHost placed in the ActivityComposable, the root of the application. The simplified composable looks like this, having removed some parameters for readability:
```kotlin
@Composable
fun ActivityComposable() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable(route = "home") {
                HomeComposable(navController)
            }
            composable(route = "detail") {
                DetailComposable(navController)
            }
        }
    }
}
```
## Store and View Interaction
### The MainActivity subscription
Since there's only one activity and its composable contains the whole hierarchy, it's up to it to register to the store changes and propagate them to the composable child.

In this way we have a single point of entry of the state and action updates, and the compose re-render system (similar to [react's virtual dom](https://it.reactjs.org/docs/faq-internals.html)) will only re-render the composable that access properties that changed since the last render cycle.

Since I usually develop apps with a lot of static data services in the same page, I also included [rxKotlin](https://github.com/ReactiveX/RxKotlin) to [debounce the state updates](https://reactivex.io/documentation/operators/debounce.html) and avoid too many unnecessary re-render.

So, these are the fields of the MainActivity:

The rxKotlin behaviour subject
```kotlin
private val appStateSubject: BehaviorSubject<AppState> =
        BehaviorSubject.createDefault(AppState())
```
The state to be passed to the composable
```kotlin
private var appState: AppState by mutableStateOf(AppState())
```
The subscription to the store
```kotlin
StoreInterface().sub("main_activity", ::handleAppState, ::handleAction)

private fun handleAppState(appState: AppState) {
        appStateSubject.onNext(appState)
    }
```
And finally, the behaviourSubject subscription that triggers on every state changes and debounces them to the mutableState of the Composable every 250ms

```kotlin
appStateSubject
    .throttleLast(
        250,
        TimeUnit.MILLISECONDS,
        AndroidSchedulers.mainThread()
    )
    .subscribe(
        { this.runOnUiThread { appState = it } },
        { Log.e("apod", it.toString()) }
    )
```
After that, it's just a matter of passing the state to the composable and then to the child:
```kotlin
setContent {
            ActivityComposable(appState)
        }
```
