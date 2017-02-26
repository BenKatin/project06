package lazyTrees;
import cs_1c.Traverser;

/**
 * Created by ben on 2/17/17.
 */

/**
 * functor to be passed into LazySearchTree
 * @param <T>   generic type
 */
public class PrintObject<T> implements Traverser<T> {


    /**
     * prints out the item
     * @param x
     */
    @Override
    public void visit(T x) {
        System.out.println(x.toString());
    }
}

