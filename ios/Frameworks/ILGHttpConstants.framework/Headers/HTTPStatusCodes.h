//
//  HTTPStatusCodes.h
//

// Based on http://en.wikipedia.org/wiki/Http_status_codes

typedef NS_ENUM(NSInteger, HTTPStatusCode) {
#pragma mark 1xx Informational
    kHTTPStatusCodeContinue = 100,
    kHTTPStatusCodeSwitchingProtocols = 101,
    kHTTPStatusCodeProcessing = 102,
    
#pragma mark 2xx Success
    kHTTPStatusCodeOK = 200,
    kHTTPStatusCodeCreated = 201,
    kHTTPStatusCodeAccepted = 202,
    kHTTPStatusCodeNonAuthoritativeInformation = 203,
    kHTTPStatusCodeNoContent = 204,
    kHTTPStatusCodeResetContent = 205,
    kHTTPStatusCodePartialContent = 206,
    kHTTPStatusCodeMultiStatus = 207,
    kHTTPStatusCodeAlreadyReported = 208,
    kHTTPStatusCodeIMUsed = 226,
    
#pragma mark 3xx Redirection
    kHTTPStatusCodeMultipleChoices = 300,
    kHTTPStatusCodeMovedPermanently = 301,
    kHTTPStatusCodeFound = 302,
    kHTTPStatusCodeSeeOther = 303,
    kHTTPStatusCodeNotModified = 304,
    kHTTPStatusCodeUseProxy = 305,
    kHTTPStatusCodeSwitchProxy = 306,
    kHTTPStatusCodeTemporaryRedirect = 307,
    kHTTPStatusCodePermanentRedirect = 308,
    
#pragma mark 4xx Client Error
    kHTTPStatusCodeBadRequest = 400,
    kHTTPStatusCodeUnauthorized = 401,
    kHTTPStatusCodePaymentRequired = 402,
    kHTTPStatusCodeForbidden = 403,
    kHTTPStatusCodeNotFound = 404,
    kHTTPStatusCodeMethodNotAllowed = 405,
    kHTTPStatusCodeNotAcceptable = 406,
    kHTTPStatusCodeProxyAuthenticationRequired = 407,
    kHTTPStatusCodeRequestTimeout = 408,
    kHTTPStatusCodeConflict = 409,
    kHTTPStatusCodeGone = 410,
    kHTTPStatusCodeLengthRequired = 411,
    kHTTPStatusCodePreconditionFailed = 412,
    kHTTPStatusCodeRequestEntityTooLarge = 413,
    kHTTPStatusCodeRequestURITooLong = 414,
    kHTTPStatusCodeUnsupportedMediaType = 415,
    kHTTPStatusCodeRequestedRangeNotSatisfiable = 416,
    kHTTPStatusCodeExpectationFailed = 417,
    kHTTPStatusCodeImATeapot = 418,
    kHTTPStatusCodeEnhanceYourCalm = 420,
    kHTTPStatusCodeUnprocessableEntity = 422,
    kHTTPStatusCodeLocked = 423,
    kHTTPStatusCodeFailedDependency = 424,
    kHTTPStatusCodeMethodFailure = 424,
    kHTTPStatusCodeUnorderedCollection = 425,
    kHTTPStatusCodeUpgradeRequired = 426,
    kHTTPStatusCodePreconditionRequired = 428,
    kHTTPStatusCodeTooManyRequests = 429,
    kHTTPStatusCodeRequestHeaderFieldsTooLarge = 431,
    kHTTPStatusCodeNoResponse = 444,
    kHTTPStatusCodeRetryWith = 449,
    kHTTPStatusCodeBlockedByWindowsParentalControls = 450,
    kHTTPStatusCodeUnavailableForLegalReasons = 451,
    kHTTPStatusCodeClientClosedRequest = 499,
    
#pragma mark 5xx Server Error
    kHTTPStatusCodeInternalServerError = 500,
    kHTTPStatusCodeNotImplemented = 501,
    kHTTPStatusCodeBadGateway = 502,
    kHTTPStatusCodeServiceUnavailable = 503,
    kHTTPStatusCodeGatewayTimeout = 504,
    kHTTPStatusCodeHTTPVersionNotSupported = 505,
    kHTTPStatusCodeVariantAlsoNegotiates = 506,
    kHTTPStatusCodeInsufficientStorage = 507,
    kHTTPStatusCodeLoopDetected = 508,
    kHTTPStatusCodeBandwidthLimitExceeded = 509,
    kHTTPStatusCodeNotExtended = 510,
    kHTTPStatusCodeNetworkAuthenticationRequired = 511,
    kHTTPStatusCodeNetworkReadTimeoutError = 598,
    kHTTPStatusCodeNetworkConnectTimeoutError = 599,
};
