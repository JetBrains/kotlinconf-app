import libs.*
import kotlinx.cinterop.*
import platform.Foundation.*

interface NetworkService {
    val baseUrl: String
    val errorHandler: (NSError) -> Unit
}

fun NetworkService.url(path: String): String = baseUrl + path

private enum class NetworkServiceError {
    JSON_PARSING_ERROR, EMPTY_BODY, WRONG_STATUS_CODE;

    fun toNSError(): NSError = NSError("NetworkService", code = ordinal.toLong(), userInfo = null)
}

fun NetworkService.plainRequest(
    request: NSURLRequest,
    requiredCodes: List<HTTPStatusCode> = emptyList(),
    handler: (NSHTTPURLResponse) -> Unit
) {
    val session = NSURLSession.sessionWithConfiguration(NSURLSessionConfiguration.defaultSessionConfiguration,
            delegate = null, delegateQueue = NSOperationQueue.mainQueue)

    log("Sending plain request: " + request.URL)

    session.dataTask(request, { _, response, error ->
        assert(NSThread.isMainThread)

        if (error != null) {
            errorHandler(error)
            return@dataTask
        }

        val httpResponse = response.uncheckedCast<NSHTTPURLResponse>()

        if (requiredCodes.isNotEmpty() && httpResponse.statusCode !in requiredCodes) {
            log("Wrong status code: ${httpResponse.statusCode} got but (${requiredCodes.joinToString()}) required")
            errorHandler(NetworkServiceError.WRONG_STATUS_CODE.toNSError())
            return@dataTask
        }

        handler(httpResponse)
    }).resume()
}

fun NetworkService.jsonRequest(request: NSURLRequest, handler: (Any) -> Unit) {
    val session = NSURLSession.sessionWithConfiguration(NSURLSessionConfiguration.defaultSessionConfiguration, 
        delegate = null, delegateQueue = NSOperationQueue.mainQueue)

    log("Sending JSON request: " + request.URL)

    session.dataTask(request, { data, _, error ->
        assert(NSThread.isMainThread)

        if (error != null) {
            errorHandler(error)
            return@dataTask
        }

        if (data == null) {
            errorHandler(NetworkServiceError.EMPTY_BODY.toNSError())
            return@dataTask
        }

        val deserializedData = NSJSONSerialization.JSONObjectWithData(data, options = 0, error = null)
        if (deserializedData != null) {
            handler(deserializedData)
        } else {
            errorHandler(NetworkServiceError.JSON_PARSING_ERROR.toNSError())
        }
    }).resume()
}
