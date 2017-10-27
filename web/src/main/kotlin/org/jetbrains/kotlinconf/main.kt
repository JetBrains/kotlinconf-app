package org.jetbrains.kotlinconf

import org.jetbrains.kotlinconf.components.*
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    window.onload = {
        render(document.getElementById("root")!!) {
            hashRouter {
                switch {
                    route("/", SessionsComponent::class, exact = true)
                    route("/session/:id", SessionComponent::class)
                }
            }
        }
    }
}