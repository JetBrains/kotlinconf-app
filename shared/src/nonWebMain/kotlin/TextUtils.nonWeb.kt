package org.jetbrains.kotlinconf.utils

import doist.x.normalize.Form
import doist.x.normalize.normalize

actual fun String.normalizeNfd(): String = normalize(Form.NFD)
