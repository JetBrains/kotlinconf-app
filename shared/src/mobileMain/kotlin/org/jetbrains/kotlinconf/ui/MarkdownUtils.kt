package org.jetbrains.kotlinconf.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownElementTypes.ATX_2
import org.intellij.markdown.MarkdownElementTypes.ATX_3
import org.intellij.markdown.MarkdownElementTypes.ATX_4
import org.intellij.markdown.MarkdownElementTypes.EMPH
import org.intellij.markdown.MarkdownElementTypes.INLINE_LINK
import org.intellij.markdown.MarkdownElementTypes.LINK_DESTINATION
import org.intellij.markdown.MarkdownElementTypes.LINK_TEXT
import org.intellij.markdown.MarkdownElementTypes.MARKDOWN_FILE
import org.intellij.markdown.MarkdownElementTypes.PARAGRAPH
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.MarkdownTokenTypes.Companion.ATX_CONTENT
import org.intellij.markdown.MarkdownTokenTypes.Companion.ATX_HEADER
import org.intellij.markdown.MarkdownTokenTypes.Companion.BLOCK_QUOTE
import org.intellij.markdown.MarkdownTokenTypes.Companion.COLON
import org.intellij.markdown.MarkdownTokenTypes.Companion.EMPH
import org.intellij.markdown.MarkdownTokenTypes.Companion.EOL
import org.intellij.markdown.MarkdownTokenTypes.Companion.EXCLAMATION_MARK
import org.intellij.markdown.MarkdownTokenTypes.Companion.HARD_LINE_BREAK
import org.intellij.markdown.MarkdownTokenTypes.Companion.HTML_TAG
import org.intellij.markdown.MarkdownTokenTypes.Companion.LBRACKET
import org.intellij.markdown.MarkdownTokenTypes.Companion.LPAREN
import org.intellij.markdown.MarkdownTokenTypes.Companion.RBRACKET
import org.intellij.markdown.MarkdownTokenTypes.Companion.RPAREN
import org.intellij.markdown.MarkdownTokenTypes.Companion.TEXT
import org.intellij.markdown.MarkdownTokenTypes.Companion.WHITE_SPACE
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import org.jetbrains.kotlinconf.ui.theme.JetBrainsSans

class MarkdownStyle(
    val linkStyle: SpanStyle = SpanStyle(
        color = Color.Blue,
        textDecoration = TextDecoration.Underline
    ),
    val h2: SpanStyle = SpanStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    val h4: SpanStyle = SpanStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    val h2Paragraph: ParagraphStyle = ParagraphStyle(
        lineHeight = 42.sp,
    ),
    val h4Paragraph: ParagraphStyle = ParagraphStyle(
        lineHeight = 24.sp,
    )
)

fun markdownString(content: String, style: MarkdownStyle = MarkdownStyle()): AnnotatedString {
    val flavour = GFMFlavourDescriptor()
    val tree = MarkdownParser(flavour).buildMarkdownTreeFromString(content)

    return buildAnnotatedString {
        appendMarkdown(tree, content, style)
    }
}

private fun AnnotatedString.Builder.appendMarkdown(
    tree: ASTNode,
    content: String,
    style: MarkdownStyle
) {
    fun ASTNode.text() = getTextInNode(content).toString()

    when (tree.type) {
        MARKDOWN_FILE, PARAGRAPH, ATX_CONTENT -> {
            for (it in tree.children) {
                appendMarkdown(it, content, style)
            }
        }

        INLINE_LINK -> {
            check(tree.children.size == 4)
            check(tree.children[0].type == LINK_TEXT)
            check(tree.children[2].type == LINK_DESTINATION)

            val linkText = tree.children[0].children[1].text()
            val destination = tree.children[2].text()
            appendMarkdownLink(linkText, destination, style.linkStyle)
        }

        TEXT, EXCLAMATION_MARK, EOL, COLON, LPAREN, RPAREN, LBRACKET, RBRACKET, WHITE_SPACE -> {
            val text = tree.text().replace("\n", "")
            append(text)
            check(tree.children.isEmpty()) { "Unexpected children: ${tree.type}:${tree.children}:'${tree.text()}'" }
        }

        HARD_LINE_BREAK -> {
            append("\n")
        }

        MarkdownTokenTypes.EMPH, MarkdownElementTypes.EMPH -> {
            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                for (it in tree.children) {
                    appendMarkdown(it, content, style)
                }
            }
        }

        HTML_TAG -> {
            when (tree.text()) {
                "<u>" -> pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                "</u>" -> pop()
                else -> error("Unsupported HTML tag: ${tree.text()}")
            }
        }

        ATX_2 -> {
            append("\n")
            withStyle(style.h2Paragraph) {
                withStyle(style.h2) {
                    for (it in tree.children) {
                        append(buildAnnotatedString {
                            appendMarkdown(it, content, style)
                        }.trim())
                    }
                }
            }
            append("\n")
        }

        ATX_3 -> {
            append("\n")
            withStyle(style.h4Paragraph) {
                withStyle(style.h4) {
                    for (it in tree.children) {
                        append(buildAnnotatedString {
                            appendMarkdown(it, content, style)
                        }.trim())
                    }
                }
            }
            append("\n")
        }

        ATX_HEADER -> {
            check(tree.children.isEmpty())
        }

        else -> error("Unsupported node type: ${tree.type}")
    }
}

internal fun AnnotatedString.Builder.appendMarkdownLink(
    text: String,
    link: String,
    linkStyle: SpanStyle
) {
    pushStringAnnotation("link", link)
    withStyle(linkStyle) {
        append(text)
    }
    pop()
}
