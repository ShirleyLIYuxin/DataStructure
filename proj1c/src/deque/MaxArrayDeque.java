package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator = null;
    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }

    public T max() {
        if (isEmpty()){
            return null;
        }
        T ret = null;
        for ( T x : this) {
            if (ret == null){
                ret = x;
            } else {
                if ((comparator != null ) && (comparator.compare(ret, x) < 0)){
                    ret = x;
                }
            }
        }
        return ret;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()){
            return null;
        }
        T ret = null;
        for ( T x : this) {
            if (ret == null){
                ret = x;
            } else {
                if ((c != null ) && (c.compare(ret, x) < 0)){
                    ret = x;
                }
            }
        }
        return ret;
    }
}
