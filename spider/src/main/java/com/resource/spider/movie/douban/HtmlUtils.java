package com.resource.spider.movie.douban;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public final class HtmlUtils {

    public static String getTextNodeValue(Node node) {
        if (null != node && (node instanceof TextNode)) {
            TextNode txtNode = (TextNode) node;
            return txtNode.text();
        }
        return null;
    }

    public static String getTextValue(Node node) {
        while (!(node instanceof TextNode)) {
            node = node.childNode(0);
        }
        return getTextNodeValue(node);
    }

}
