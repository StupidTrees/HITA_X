package com.stupidtree.hitax.utils

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

object HTMLUtils {
    fun getStringValueByClass(d: Element, className: String?): String {
        return try {
            d.getElementsByClass(className).first().text()
        } catch (e: Exception) {
            ""
        }
    }

    fun getAttrValueByClass(d: Element, className: String?, attr: String?): String {
        return try {
            d.getElementsByClass(className).first().attr(attr)
        } catch (e: Exception) {
           ""
        }
    }

    fun getAttrValueInTag(d: Element, attr: String?, tag: String?): String {
        return try {
            d.getElementsByTag(tag).first().attr(attr)
        } catch (e: Exception) {
            ""
        }
    }

    fun getTextOfTag(d: Element, tag: String?): String {
        return try {
            d.getElementsByTag(tag).first().text()
        } catch (e: Exception) {
           ""
        }
    }

    fun getTextOfTagHavingAttr(d: Element, tag: String?, attr: String?): String {
        return try {
            for (e in d.getElementsByTag(tag)) {
                if (e.hasAttr(attr)) return e.text()
            }
            ""
        } catch (e: Exception) {
            ""
        }
    }

    fun getElementsInClassByTag(d: Document, className: String?, tag: String?): Elements {
        return try {
            d.getElementsByClass(className).first().getElementsByTag(tag)
        } catch (e: Exception) {
            e.printStackTrace()
            Elements()
        }
    }
}