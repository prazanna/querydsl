/*
 * Copyright 2011, Mysema Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mysema.query.types.path;

import java.lang.reflect.AnnotatedElement;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

import com.mysema.query.types.Expression;
import com.mysema.query.types.Ops;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathImpl;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.PathMetadataFactory;
import com.mysema.query.types.Visitor;
import com.mysema.query.types.expr.ArrayExpression;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.NumberOperation;
import com.mysema.query.types.expr.SimpleExpression;

/**
 * ArrayPath represents an array typed path
 *
 * @author tiwe
 *
 * @param <E> component type
 */
public class ArrayPath<E> extends SimpleExpression<E[]> implements Path<E[]>, ArrayExpression<E> {

    private static final long serialVersionUID = 7795049264874048226L;

    private final Class<E> componentType;

    private final PathImpl<E[]> pathMixin;

    @Nullable
    private volatile NumberExpression<Integer> size;

    public ArrayPath(Class<? super E[]> type, String variable) {
        this(type, PathMetadataFactory.forVariable(variable));
    }
    
    public ArrayPath(Class<? super E[]> type, Path<?> parent, String property) {
        this(type, PathMetadataFactory.forProperty(parent, property));
    }
    
    @SuppressWarnings("unchecked")
    public ArrayPath(Class<? super E[]> type, PathMetadata<?> metadata) {
        super(new PathImpl<E[]>((Class)type, metadata));
        this.pathMixin = (PathImpl<E[]>)mixin;
        this.componentType = (Class<E>)type.getComponentType();
    }

    @Override
    public final <R,C> R accept(Visitor<R,C> v, C context) {
        return v.visit(pathMixin, context);
    }
    
    /**
     * Create a expression for indexed access
     *
     * @param index
     * @return
     */
    public SimplePath<E> get(Expression<Integer> index) {
        PathMetadata<Integer> md = PathMetadataFactory.forArrayAccess(pathMixin, index);
        return new SimplePath<E>(componentType, md);
    }

    /**
     * Create a expression for indexed access
     *
     * @param index
     * @return
     */
    public SimplePath<E> get(@Nonnegative int index) {
        PathMetadata<Integer> md = PathMetadataFactory.forArrayAccess(pathMixin, index);
        return new SimplePath<E>(componentType, md);
    }

    public Class<E> getElementType() {
        return componentType;
    }

    @Override
    public PathMetadata<?> getMetadata() {
        return pathMixin.getMetadata();
    }

    @Override
    public Path<?> getRoot() {
        return pathMixin.getRoot();
    }

    @Override
    public AnnotatedElement getAnnotatedElement() {
        return pathMixin.getAnnotatedElement();
    }

    /**
     * Create an expression for the array size
     *
     * @return
     */
    public NumberExpression<Integer> size() {
        if (size == null) {
            size = NumberOperation.create(Integer.class, Ops.ARRAY_SIZE, pathMixin);
        }
        return size;
    }

}
