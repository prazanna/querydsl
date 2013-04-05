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
package com.mysema.query.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;


/**
 * ExpressionUtils provides utilities for constructing common operation instances. This class is 
 * used internally in Querydsl and is not suitable to be used in cases where DSL methods are needed,
 * since the Expression implementations used in this class are minimal internal implementations.
 * 
 * @author tiwe
 *
 */
public final class ExpressionUtils {
        
    /**
     * @param col
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Expression<T> all(CollectionExpression<?, ? super T> col) {
        return OperationImpl.create((Class<T>)col.getParameter(0), Ops.QuantOps.ALL, col);
    }

    /**
     * @param col
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Expression<T> any(CollectionExpression<?, ? super T> col) {
        return OperationImpl.create((Class<T>)col.getParameter(0), Ops.QuantOps.ANY, col);
    }
            
    /**
     * Create the intersection of the given arguments
     * 
     * @param exprs
     * @return
     */
    @Nullable
    public static Predicate allOf(Collection<Predicate> exprs) {
        Predicate rv = null;
        for (Predicate b : exprs) {
            if (b != null) {
                rv = rv == null ? b : ExpressionUtils.and(rv,b);    
            }            
        }
        return rv;
    }
    
    /**
     * Create the intersection of the given arguments
     * 
     * @param exprs
     * @return
     */
    @Nullable
    public static Predicate allOf(Predicate... exprs) {
        Predicate rv = null;
        for (Predicate b : exprs) {
            if (b != null) {
                rv = rv == null ? b : ExpressionUtils.and(rv,b);    
            }            
        }
        return rv;
    }

    /**
     * Create the intersection of the given arguments
     * 
     * @param left
     * @param right
     * @return
     */
    public static Predicate and(Predicate left, Predicate right) {
        return PredicateOperation.create(Ops.AND, left, right);
    }
    

    /**
     * Create the union of the given arguments
     * 
     * @param exprs
     * @return
     */
    @Nullable
    public static Predicate anyOf(Collection<Predicate> exprs) {
        Predicate rv = null;
        for (Predicate b : exprs) {
            if (b != null) {
                rv = rv == null ? b : ExpressionUtils.or(rv,b);    
            }            
        }
        return rv;
    }
    
    /**
     * Create the union of the given arguments
     * 
     * @param exprs
     * @return
     */
    @Nullable
    public static Predicate anyOf(Predicate... exprs) {
        Predicate rv = null;
        for (Predicate b : exprs) {
            if (b != null) {
                rv = rv == null ? b : ExpressionUtils.or(rv,b);    
            }            
        }
        return rv;
    }
        
    /**
     * Create an alias expression (source as alias) with the given source and alias
     * 
     * @param <D>
     * @param source
     * @param alias
     * @return
     */
    public static <D> Expression<D> as(Expression<D> source, Path<D> alias) {
        return OperationImpl.create(alias.getType(), Ops.ALIAS, source, alias);
    }
    
    /**
     * Create an alias expression (source as alias) with the given source and alias
     * 
     * @param <D>
     * @param source
     * @param alias
     * @return
     */
    public static <D> Expression<D> as(Expression<D> source, String alias) {
        return as(source, new PathImpl<D>(source.getType(), alias));
    }

    /**
     * @param source
     * @return
     */
    public static Expression<Long> count(Expression<?> source) {
        return OperationImpl.create(Long.class, Ops.AggOps.COUNT_AGG, source);
    }
    
    /**
     * Create an left equals constant expression
     * 
     * @param <D>
     * @param left
     * @param constant
     * @return
     */
    public static <D> Predicate eqConst(Expression<D> left, D constant) {
        return eq(left, new ConstantImpl<D>(constant));
    }
    
    /**
     * Create an left equals right expression
     * 
     * @param <D>
     * @param left
     * @param right
     * @return
     */
    public static <D> Predicate eq(Expression<D> left, Expression<? extends D> right) {
        return PredicateOperation.create(Ops.EQ, left, right);
    }
    
    /**
     * Create an left in right expression
     * 
     * @param <D>
     * @param left
     * @param right
     * @return
     */
    public static <D> Predicate in(Expression<D> left, CollectionExpression<?,? extends D> right) {
        return PredicateOperation.create(Ops.IN, left, right);
    }
    
    /**
     * Create an left in right expression
     * 
     * @param <D>
     * @param left
     * @param right
     * @return
     */
    public static <D> Predicate in(Expression<D> left, Collection<? extends D> right) {
        if (right.size() == 1) {
            return eqConst(left, right.iterator().next());
        } else {
            return PredicateOperation.create(Ops.IN, left, new ConstantImpl<Collection<?>>(right));
        }
    }
    
    /**
     * Create a left is null expression
     * 
     * @param left
     * @return
     */
    public static Predicate isNull(Expression<?> left) {
        return PredicateOperation.create(Ops.IS_NULL, left);
    }
    
    /**
     * Create a left is not null expression
     * 
     * @param left
     * @return
     */
    public static Predicate isNotNull(Expression<?> left) {
        return PredicateOperation.create(Ops.IS_NOT_NULL, left);
    }   
    
    /**
     * Convert the given like pattern to a regex pattern
     * 
     * @param expr
     * @return
     */    
    public static Expression<String> likeToRegex(Expression<String> expr) {
        return likeToRegex(expr, true);
    }
    

    @SuppressWarnings("unchecked")
    public static Expression<String> likeToRegex(Expression<String> expr, boolean matchStartAndEnd) {
        // TODO : this should take the escape character into account
        if (expr instanceof Constant<?>) {
            final String like = expr.toString();
            final StringBuilder rv = new StringBuilder(like.length() + 4);
            if (matchStartAndEnd && !like.startsWith("%")) {
                rv.append('^');
            }
            for (int i = 0; i < like.length(); i++) {
                char ch = like.charAt(i);                
                if (ch == '.' || ch == '*' || ch == '?') {
                    rv.append('\\');
                } else if (ch == '%') {
                    rv.append(".*"); 
                    continue;
                } else if (ch == '_') {
                    rv.append('.'); 
                    continue;
                }
                rv.append(ch);
            }
            if (matchStartAndEnd && !like.endsWith("%")) {
                rv.append('$');
            }
            if (!like.equals(rv.toString())) {
                return ConstantImpl.create(rv.toString());    
            }            
        } else if (expr instanceof Operation<?>) {
            Operation<?> o = (Operation<?>)expr;
            if (o.getOperator() == Ops.CONCAT) {
                Expression<String> lhs = likeToRegex((Expression<String>) o.getArg(0), false);
                Expression<String> rhs = likeToRegex((Expression<String>) o.getArg(1), false);
                if (lhs != o.getArg(0) || rhs != o.getArg(1)) {
                    return OperationImpl.create(String.class, Ops.CONCAT, lhs, rhs);    
                }                
            }
        }
        return expr;
    }
    
    /**
     * @param exprs
     * @return
     */
    public static <T> Expression<T> list(Class<T> clazz, Expression<?>... exprs) {
        return list(clazz, ImmutableList.copyOf(exprs));
    }
    

    /**
     * @param exprs
     * @return
     */
    public static <T> Expression<T> list(Class<T> clazz, List<? extends Expression<?>> exprs) {        
        Expression<T> rv = (Expression<T>)exprs.get(0);
        for (int i = 1; i < exprs.size(); i++) {
            rv = OperationImpl.create(clazz, Ops.LIST, rv, exprs.get(i));
        }
        return rv;
    }    
        
    @SuppressWarnings("unchecked")
    public static Expression<String> regexToLike(Expression<String> expr) {
        if (expr instanceof Constant<?>) {           
            final String str = expr.toString();
            final StringBuilder rv = new StringBuilder(str.length() + 2);
            for (int i = 0; i < str.length(); i++) {
                final char ch = str.charAt(i);
                if (ch == '.') {
                    if (i < str.length() - 1 && str.charAt(i+1) == '*') {
                        rv.append('%');
                        i++;
                    } else {
                        rv.append('_');
                    }
                    continue;
                }
                rv.append(ch);
            }
            if (!rv.toString().equals(str)) {
                return ConstantImpl.create(rv.toString());
            }            
        } else if (expr instanceof Operation<?>) {
            Operation<?> o = (Operation<?>)expr;
            if (o.getOperator() == Ops.CONCAT) {
                Expression<String> lhs = regexToLike((Expression<String>) o.getArg(0));
                Expression<String> rhs = regexToLike((Expression<String>) o.getArg(1));
                if (lhs != o.getArg(0) || rhs != o.getArg(1)) {
                    return OperationImpl.create(String.class, Ops.CONCAT, lhs, rhs);    
                }                
            }            
        }
        return expr;
    }
    
    /**
     * Create a left not equals constant expression
     * 
     * @param <D>
     * @param left
     * @param constant
     * @return
     */
    public static <D> Predicate neConst(Expression<D> left, D constant) {
        return ne(left, new ConstantImpl<D>(constant));
    }
    
    /**
     * Create a left not equals right expression
     * 
     * @param <D>
     * @param left
     * @param right
     * @return
     */
    public static <D> Predicate ne(Expression<D> left, Expression<? super D> right) {
        return PredicateOperation.create(Ops.NE, left, right);
    }
    
    /**
     * Create a left or right expression
     * 
     * @param left
     * @param right
     * @return
     */
    public static Predicate or(Predicate left, Predicate right) {
        return PredicateOperation.create(Ops.OR, left, right);
    }
    
    /**
     * @param args
     * @return
     */
    public static ImmutableList<Expression<?>> distinctList(Expression<?>... args) {
        final ImmutableList.Builder<Expression<?>> builder = ImmutableList.builder();
        final Set<Expression<?>> set = new HashSet<Expression<?>>(args.length);
        for (Expression<?> arg : args) {
            if (set.add(arg)) {
                builder.add(arg);
            }
        }
        return builder.build();
    }
    
    /**
     * @param args
     * @return
     */
    public static ImmutableList<Expression<?>> distinctList(Expression<?>[]... args) {
        final ImmutableList.Builder<Expression<?>> builder = ImmutableList.builder();
        final Set<Expression<?>> set = new HashSet<Expression<?>>();
        for (Expression<?>[] arr : args) {
            for (Expression<?> arg : arr) {
                if (set.add(arg)) {
                    builder.add(arg);
                }
            }
        }
        return builder.build();
    }
    
    /**
     * @param expr
     * @return
     */
    public static <T> Expression<T> extract(Expression<T> expr) {
        final Class<?> clazz = expr.getClass();
        if (clazz == PathImpl.class || clazz == PredicateOperation.class || clazz == ConstantImpl.class) {
            return expr;
        } else {
            return (Expression<T>) expr.accept(ExtractorVisitor.DEFAULT, null);    
        }        
    }
    
    private ExpressionUtils() {}
    
}
