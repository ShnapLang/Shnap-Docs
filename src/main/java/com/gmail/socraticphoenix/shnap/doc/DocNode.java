/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 socraticphoenix@gmail.com
 * Copyright (c) 2017 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gmail.socraticphoenix.shnap.doc;

import com.gmail.socraticphoenix.pio.ByteStream;
import com.gmail.socraticphoenix.pio.Bytes;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DocNode {
    private Doc doc;
    private Map<String, DocNode> children;

    public DocNode(Doc doc, Map<String, DocNode> children) {
        this.doc = doc;
        this.children = children;
    }

    public DocNode(Doc doc) {
        this(doc, new LinkedHashMap<>());
    }

    public Doc getDoc() {
        return this.doc;
    }

    public Map<String, DocNode> getChildren() {
        return this.children;
    }

    public void write(ByteStream stream) throws IOException {
        this.doc.write(stream);
        stream.putInt(this.children.size());
        for (Map.Entry<String, DocNode> child : this.children.entrySet()) {
            Bytes.writeString(stream, child.getKey());
            child.getValue().write(stream);
        }
    }

    public static DocNode read(ByteStream stream) throws IOException {
        Doc doc = Doc.read(stream);
        int childSize = stream.getInt();
        Map<String, DocNode> children = new LinkedHashMap<>();
        for (int i = 0; i < childSize; i++) {
            String key = Bytes.readString(stream);
            DocNode node = DocNode.read(stream);
            children.put(key, node);
        }
        return new DocNode(doc, children);
    }

}
