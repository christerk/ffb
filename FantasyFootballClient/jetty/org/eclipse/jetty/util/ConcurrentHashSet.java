//
//  ========================================================================
//  Copyright (c) 1995-2013 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>
{
    private final Map<E, Boolean> _map = new ConcurrentHashMap<E, Boolean>();
    private transient Set<E> _keys = _map.keySet();

    public ConcurrentHashSet()
    {
    }

    
    public boolean add(E e)
    {
        return _map.put(e,Boolean.TRUE) == null;
    }

    
    public void clear()
    {
        _map.clear();
    }

    
    public boolean contains(Object o)
    {
        return _map.containsKey(o);
    }

    
    public boolean containsAll(Collection<?> c)
    {
        return _keys.containsAll(c);
    }

    
    public boolean equals(Object o)
    {
        return o == this || _keys.equals(o);
    }

    
    public int hashCode()
    {
        return _keys.hashCode();
    }

    
    public boolean isEmpty()
    {
        return _map.isEmpty();
    }

    
    public Iterator<E> iterator()
    {
        return _keys.iterator();
    }

    
    public boolean remove(Object o)
    {
        return _map.remove(o) != null;
    }

    
    public boolean removeAll(Collection<?> c)
    {
        return _keys.removeAll(c);
    }

    
    public boolean retainAll(Collection<?> c)
    {
        return _keys.retainAll(c);
    }

    
    public int size()
    {
        return _map.size();
    }

    
    public Object[] toArray()
    {
        return _keys.toArray();
    }

    
    public <T> T[] toArray(T[] a)
    {
        return _keys.toArray(a);
    }

    
    public String toString()
    {
        return _keys.toString();
    }
}
