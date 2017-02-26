package lazyTrees;

/**
 * Created by ben on 2/17/17.
 */
import cs_1c.Traverser;

import java.util.*;

/**
 * A binary search tree that utilises lazy deletion
 * @param <E> generic data type
 */

public class LazySearchTree<E extends Comparable< ? super E > >
        implements Cloneable
{
    protected int mSize;
    protected int mSizeHard;
    protected LazySTNode mRoot;

    public LazySearchTree() { clear(); }
    public boolean empty() { return (mSize == 0); }
    public int size() { return mSize; }
    public int sizeHard(){ return mSizeHard; }
    public void clear() { mSize = 0; mRoot = null;mSizeHard = 0; }
    public int showHeight() { return findHeight(mRoot, -1); }

    /**
     * private inner node class
     */

    private class LazySTNode
    {
        // use public access so the tree or other classes can access members
        private LazySTNode lftChild, rtChild;
        private boolean deleted;
        private E data;
        private LazySTNode myRoot;  // needed to test for certain error

        public LazySTNode(E d, LazySTNode lft, LazySTNode rt )
        {
            lftChild = lft;
            rtChild = rt;
            data = d;
            deleted = false;
        }

        public LazySTNode()
        {
            this(null, null, null);
        }

        // function stubs -- for use only with AVL Trees when we extend
        public int getHeight() { return 0; }
        boolean setHeight(int height) { return true; }
        public LazySTNode getLeft(){
            return lftChild;
        }
        public LazySTNode getRight(){
            return rtChild;
        }
        public void setLeft(LazySTNode newNode){
            lftChild = newNode;
        }
        public void setRight(LazySTNode newNode){
            rtChild = newNode;
        }
    }

    /**
     * @return returns the minimum value in the BST
     */
    public E findMin()
    {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMin(mRoot).data;
    }

    /**
     *
     * @return returns the maximum value in the BST
     */
    public E findMax()
    {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findMax(mRoot).data;
    }

    protected E findHardMin(){
        if (mRoot == null)
            throw new NoSuchElementException();
        return findHardMin(mRoot).data;
    }

    private LazySTNode findHardMin(LazySTNode mRoot) {

        if(mRoot.lftChild == null){
            return mRoot;
        }

        return findHardMin(mRoot.lftChild);

    }



    protected E findHardMax()
    {
        if (mRoot == null)
            throw new NoSuchElementException();
        return findHardMax(mRoot).data;
    }

    private LazySTNode findHardMax(LazySTNode mRoot) {
        if(mRoot == null){
            return mRoot;
        }

        return findHardMax(mRoot.rtChild);
    }

    public boolean collectGarbage(){

        if (mRoot == null)
            throw new NoSuchElementException();
        return collectGarbage(mRoot);
    }

    private boolean collectGarbage(LazySTNode mRoot) {

        if(mRoot == null)
            return false;

        boolean lft,rt,me;
        me = false;

        lft = collectGarbage(mRoot.lftChild);



        rt = collectGarbage(mRoot.rtChild);

        if(mRoot.deleted){
            mRoot = removeHard(mRoot,mRoot.data);
            me=true;

        }

        return(lft||rt||me);
    }

    protected LazySTNode removeHard( LazySTNode root, E x  )
    {
        int compareResult;

        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);
        if ( compareResult < 0 )
            root.lftChild = remove(root.lftChild, x);
        else if ( compareResult > 0 )
            root.rtChild = remove(root.rtChild, x);

            // found the node
        else if (root.lftChild != null && root.rtChild != null)
        {
            root.data = findHardMin(root.rtChild).data;
            root.rtChild = removeHard(root.rtChild, root.data);
        }
        else
        {
            root =
                    (root.getLeft() != null)? root.getLeft() : root.getRight();
            mSizeHard--;
        }
        return root;
    }

    /**
     * finds if there is a node in the BST that contains the data X
     * @param x     data to be search for
     * @return      returns the node that contains the data, if it exists
     */
    public E find( E x )
    {
        LazySTNode resultNode;
        resultNode = find(mRoot, x);
        if (resultNode == null)
            throw new NoSuchElementException();
        return resultNode.data;
    }

    /**
     * returns a boolean if the data is in the BST
     * @param x     data to be searched for
     * @return      a boolean
     */
    public boolean contains(E x)  { return find(mRoot, x) != null; }

    /**
     * adds the data to the BST in a new node by calling a private class
     * @param x     data to be added
     * @return
     */
    public boolean insert( E x )
    {
        int oldSize = mSize;
        mRoot = insert(mRoot, x);
        return (mSize != oldSize);
    }

    /**
     * searches though the BST, and lazily removes a node if its data matches data x
     * @param x
     * @return
     */
    public boolean remove( E x )
    {
        int oldSize = mSize;
        mRoot = remove(mRoot, x);
        return (mSize != oldSize);
    }

    /**
     * traverses all current nodes, as well as nodes that were added and then deleted
     * @param func      functor that can access the data
     * @param <F>       generic object
     */
    public < F extends Traverser<? super E >>
    void traverseHard(F func)
    {
        traverseHard(func, mRoot);
    }

    /**
     * transverses only current nodes, and not deleted nodes
     * @param func      functor that can access the data
     * @param <F>       generic type
     */
    public < F extends Traverser<? super E >>
    void traverseSoft(F func)
    {
        traverseSoft(func, mRoot);
    }

    /**
     * clones the BST with a copy constructor
     * @return
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException
    {
        LazySearchTree<E> newObject = (LazySearchTree<E>)super.clone();
        newObject.clear();  // can't point to other's data

        newObject.mRoot = cloneSubtree(mRoot);
        newObject.mSize = mSize;
        newObject.mSizeHard = mSizeHard;

        return newObject;
    }
    //returns a  boolean for if any non deleted nodes below root
    private boolean existsBelow(LazySTNode root){
        if(root == null)
            return false;
        if(root.deleted == false)
            return true;

        return (existsBelow(root.lftChild) || existsBelow(root.rtChild));

    }

    // private helper methods ----------------------------------------

    //uses existsbelow to look for a minimum
    protected LazySTNode findMin(LazySTNode root )
    {
        if (root == null)
            return null;
        if (existsBelow(root.lftChild) == true)
            return findMin(root.lftChild);
        if(root.deleted == true)
            return findMin(root.rtChild);
        return root;
    }
    //uses existBelow to look for a maximum
    protected LazySTNode findMax(LazySTNode root )
    {
        if (root == null)
            return null;
        if(existsBelow(root.rtChild) == true)
            return findMax(root.rtChild);
        if (root.deleted == true)
            return findMax(root.lftChild);
        return root;
    }
    //inserts data into the BST
    protected LazySTNode insert(LazySTNode root, E x )
    {
        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
        {
            mSize++;
            mSizeHard++;
            return new LazySTNode(x, null, null);
        }

        compareResult = x.compareTo(root.data);
        if ( compareResult < 0 )
            root.lftChild = insert(root.lftChild, x);
        else if ( compareResult > 0 )
            root.rtChild = insert(root.rtChild, x);
        else if(root.deleted == true){
            root.deleted = false;
            mSize++;
        }

        return root;
    }
    //marks a node in the BST as deleted
    protected LazySTNode remove(LazySTNode root, E x  )
    {
        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);
        if ( compareResult < 0 )
            root.lftChild = remove(root.lftChild, x);
        else if ( compareResult > 0 )
            root.rtChild = remove(root.rtChild, x);
        else{
            root.deleted = true;
            mSize--;
        }
            // found the node

        return root;
    }
    //recursivly goes through all node and acesses their data
    protected <F extends Traverser<? super E>>
    void traverseHard(F func, LazySTNode treeNode)
    {
        if (treeNode == null)
            return;

        traverseHard(func, treeNode.lftChild);
        func.visit(treeNode.data);
        traverseHard(func, treeNode.rtChild);
    }
    //recusrsivley goes through all nodes, but only accesses the data of non-deleted nodes
    protected <F extends Traverser<? super E>>
    void traverseSoft(F func, LazySTNode treeNode)
    {
        if (treeNode == null)
            return;

        traverseSoft(func, treeNode.lftChild);
        if(treeNode.deleted == false)
            func.visit(treeNode.data);

        traverseSoft(func, treeNode.rtChild);
    }
    //Finds the node that contains data matching the data passed in, as long as it is not deleted
    protected LazySTNode find(LazySTNode root, E x )
    {
        int compareResult;  // avoid multiple calls to compareTo()

        if (root == null)
            return null;

        compareResult = x.compareTo(root.data);
        if (compareResult < 0)
            return find(root.lftChild, x);
        if (compareResult > 0)
            return find(root.rtChild, x);
        if(root.deleted == false){
            return root;   // found
        }
        return null;
    }

    protected LazySTNode cloneSubtree(LazySTNode root)
    {
        LazySTNode newNode;
        if (root == null)
            return null;

        // does not set myRoot which must be done by caller
        newNode = new LazySTNode
                (
                        root.data,
                        cloneSubtree(root.lftChild),
                        cloneSubtree(root.rtChild)
                );
        return newNode;
    }

    protected int findHeight(LazySTNode treeNode, int height )
    {
        int leftHeight, rightHeight;
        if (treeNode == null)
            return height;
        height++;
        leftHeight = findHeight(treeNode.lftChild, height);
        rightHeight = findHeight(treeNode.rtChild, height);
        return (leftHeight > rightHeight)? leftHeight : rightHeight;
    }
}
