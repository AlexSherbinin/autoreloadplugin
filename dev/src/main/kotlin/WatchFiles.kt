import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import kotlin.io.path.Path
import kotlin.system.exitProcess

@OptIn(DelicateCoroutinesApi::class)
fun watchFiles() {
    GlobalScope.launch {
        val watchFiles = System.getProperty("watchFiles")?.split(";") ?: return@launch
        val watchFlow = watchFiles
            .map { Path(it).eventsFlow }
            .let { merge(*it.toTypedArray()) }
        watchFlow.collect {
            it.reset()
            exitProcess(0)
        }
    }
}


private fun Path.eventsFlowFor(vararg eventKinds: WatchEvent.Kind<*>) = flow {
    val watchService = fileSystem.newWatchService()
    register(watchService, eventKinds)
    while (true) {
        emit(watchService.take())
    }
}.flowOn(Dispatchers.IO)

private val Path.eventsFlow: Flow<WatchKey>
    get() = flow {
        if (toFile().isDirectory) {
            val childFlows = toFile().listFiles()?.map { it.toPath().eventsFlow } ?: listOf()
            emitAll(
                merge(
                    *childFlows.toTypedArray(),
                    eventsFlowFor(
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY
                    )
                )
            )
        }
    }.flowOn(Dispatchers.IO)