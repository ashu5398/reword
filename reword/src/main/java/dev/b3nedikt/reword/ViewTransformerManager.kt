package dev.b3nedikt.reword

import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import dev.b3nedikt.reword.transformer.ViewTransformer

/**
 * Manages all view transformers
 */
internal class ViewTransformerManager {

    private val transformers = mutableListOf<ViewTransformer<View>>()

    /**
     * Register a new view viewTransformer to be applied on newly inflating views.
     *
     * @param viewTransformer to be added to transformers list.
     */
    @Suppress("UNCHECKED_CAST")
    fun registerTransformer(viewTransformer: ViewTransformer<*>) {
        transformers.add(viewTransformer as ViewTransformer<View>)
    }

    /**
     * Transforms a view.
     * it tries to find proper transformers for view, and if exists, it will apply them on view,
     * and return the final result as a new view.
     *
     * @param view  to be transformed.
     * @param attrs attributes of the view.
     * @return the transformed view.
     */
    fun transform(view: View, attrs: AttributeSet): View =
            transformers.find { it.viewType.isInstance(view) }
                    ?.run {
                        val extractedAttributes = extractAttributes(view, attrs)
                        view.setTag(R.id.view_tag, extractedAttributes)
                        view.transform(extractedAttributes)
                        view
                    }
                    ?: view

    /**
     * Transforms all children of the provided view.
     * Tries to find proper transformers for each child view, and if exists, it will apply them on
     * the view in place. Implemented not recursive, to ensure we only visit each child once
     * for efficiency.
     */
    fun transformChildren(parentView: View) {
        val visited = mutableListOf<View>()
        val unvisited = mutableListOf(parentView)

        while (unvisited.isNotEmpty()) {
            val child = unvisited.removeAt(0)

            @Suppress("UNCHECKED_CAST")
            val attrsTag = child.getTag(R.id.view_tag) as? Map<String, Int>

            attrsTag?.let { attrs ->
                transformers.find { it.viewType.isInstance(child) }
                        ?.run { child.transform(attrs) }
            }

            visited.add(child)
            if (child !is ViewGroup) continue
            val childCount = child.childCount
            for (i in 0 until childCount) unvisited.add(child.getChildAt(i))
        }
    }
}