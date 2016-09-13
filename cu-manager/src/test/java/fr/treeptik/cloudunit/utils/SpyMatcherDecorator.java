package fr.treeptik.cloudunit.utils;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class SpyMatcherDecorator<T> extends TypeSafeMatcher<T> {
    private T matchedValue;
    
    private Matcher<T> decoratedMatcher;
    
    public SpyMatcherDecorator() {
        this(CoreMatchers.anything());
    }
    
    public SpyMatcherDecorator(Matcher<T> decoratedMatcher) {
        this.decoratedMatcher = decoratedMatcher;
    }
    
    @Override
    public void describeTo(Description description) {
        decoratedMatcher.describeTo(description);
    }

    @Override
    protected boolean matchesSafely(T item) {
        matchedValue = item;
        
        return decoratedMatcher.matches(item);
    }
    
    public T getMatchedValue() {
        return matchedValue;
    }

}
