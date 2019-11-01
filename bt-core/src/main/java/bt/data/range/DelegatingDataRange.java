/*
 * Copyright (c) 2016—2017 Andrei Tomashpolskiy and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bt.data.range;

import bt.data.DataRange;
import bt.data.DataRangeVisitor;
import bt.net.buffer.ByteBufferView;

import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * Adapter to a range, that indirectly encapsulates DataRange (most probably via delegation chain).
 *
 * @since 1.3
 */
class DelegatingDataRange<T extends Range<T>> implements DataRange, DelegatingRange<T> {

    private Range<T> delegate;
    private Function<Range<T>, DataRange> converter;

    @SuppressWarnings("unchecked")
    static <E extends Range<E>> DelegatingDataRange range(E delegate, Function<E, DataRange> converter) {
        return new DelegatingDataRange<>(delegate, r -> converter.apply((E)r));
    }

    private DelegatingDataRange(Range<T> delegate, Function<Range<T>, DataRange> converter) {
        this.delegate = delegate;
        this.converter = converter;
    }

    @Override
    public void visitUnits(DataRangeVisitor visitor) {
        converter.apply(delegate).visitUnits(visitor);
    }

    @Override
    public long length() {
        return delegate.length();
    }

    @Override
    public DataRange getSubrange(long offset, long length) {
        return new DelegatingDataRange<>(delegate.getSubrange(offset, length), converter);
    }

    @Override
    public DataRange getSubrange(long offset) {
        return new DelegatingDataRange<>(delegate.getSubrange(offset), converter);
    }

    @Override
    public byte[] getBytes() {
        return delegate.getBytes();
    }

    @Override
    public boolean getBytes(ByteBuffer buffer) {
        return delegate.getBytes(buffer);
    }

    @Override
    public void putBytes(byte[] block) {
        delegate.putBytes(block);
    }

    @Override
    public void putBytes(ByteBufferView buffer) {
        delegate.putBytes(buffer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getDelegate() {
        return (T) delegate;
    }
}
