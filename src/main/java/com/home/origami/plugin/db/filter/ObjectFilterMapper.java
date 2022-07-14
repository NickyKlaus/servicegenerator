package com.home.origami.plugin.db.filter;

import org.dizitart.no2.objects.ObjectFilter;

public interface ObjectFilterMapper {
    ObjectFilter map(Filter filter);
}
