import java.util.HashSet;
import java.util.Set;
import Math;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap {
    private HeapNode firstTree;
    private int size = 0;
    private static int cuts = 0;
    private static int links = 0;
    private int numOfTrees = 0;
    private int marks = 0;
    private HeapNode min = null;

    /**
     * public boolean isEmpty()
     * precondition: none
     * The method returns true if and only if the heap
     * is empty.
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * public HeapNode insert(int key)
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * Returns the new node created.
     */
    public HeapNode insert(int key) {
        this.size++;
        HeapNode node = new HeapNode(key);
        insertFirst(node);
        return node;
    }
    /**
     * private void insertLast(HeapNode node)
     *
     * inserts the given node as the last tree of the heap and changes the node and heap's fields according to the change.
     *O(1)
     */
    private void insertFirst(HeapNode node) {
        if (numOfTrees!=0) {
            insertAsSibling(node,firstTree);
            firstTree=node;
        } else {
            firstTree = node;
            node.next=node;
            node.prev=node;
            min = node;
        }
        if(node.mark){
            node.mark = false;
            marks--;
        }
        if (node.key < this.min.key) {
            this.min = node;
        }
        this.numOfTrees++;
        node.isARoot=true;

    }

    /**
     * public void deleteMin()
     * Delete the node containing the minimum key.
     * does the successiveLinking procedure.
     * O(n)
     */
    public void deleteMin() {
        this.size--;
        HeapNode min = this.min;
        this.numOfTrees--;
        this.numOfTrees += min.rank;
        if(min.rank==0){
            if(numOfTrees==0){
                firstTree=null;
                this.min=null;
            }
            else{
                setSibling(min.prev,min.next);
                if(min==this.firstTree){
                    this.firstTree=min.next;
                }
            }
        }
        else {
            if(numOfTrees>min.rank){
                setSibling(min.child.prev,min.next);
                setSibling(min.prev,min.child);
                if(min==this.firstTree){
                    this.firstTree=min.child;
                }
            }
            else{
                this.firstTree=min.child;
            }
        }
        successiveLinking();
    }
    /**
     * public void setSibling(HeapNode left, HeapNode right)
     *
     * sets the first argument as the left sibling of the second.
     *O(1)
     */
    public void setSibling(HeapNode left, HeapNode right) {
        right.prev = left;
        left.next = right;
    }
    /**
     * public void successiveLinking()
     *
     * links all the trees in the heap according to the procedure shown in the lecture.
     *O(n)
     */
    public void successiveLinking() {
        if(this.size==0){
            return;
        }
        HeapNode[] arrayOfTrees = new HeapNode[this.size];
        HeapNode[] originalTrees = new HeapNode[this.numOfTrees];
        HeapNode node = this.firstTree;
        int i = 0;
        do{
            originalTrees[i] = node;
            node = node.next;
            i++;
        }while(node!=this.firstTree);
        i = 0;
        node = originalTrees[0];
        int highestRank = 0;
        while (node != null) {
            if (node.rank > highestRank) {
                highestRank=node.rank;
            }
            if(arrayOfTrees[node.rank]!=null){
                node= link(node,arrayOfTrees[node.rank]);
                arrayOfTrees[node.rank-1]=null;
            }
            else {
                arrayOfTrees[node.rank]=node;
                i++;
                if(i<originalTrees.length){
                    node=originalTrees[i];
                }
                else{
                    break;
                }

            }

        }
        insertFromArray(arrayOfTrees, highestRank);
    }
    /**
     * public void insertFromArray (HeapNode[]arrayOfTrees,int highestRank)
     *
     * Initializes the heap and inserts all nodes from array as trees.
     *O(highestRank)=O(log n)
     */
    public void insertFromArray (HeapNode[]arrayOfTrees,int highestRank){
        this.firstTree = null;
        numOfTrees = 0;
        this.min=null;
        for (int i = highestRank; i >=0; i--) {
            if (arrayOfTrees[i] != null) {
                insertFirst(arrayOfTrees[i]);
            }
        }
    }

    /**
     * public HeapNode findMin()
     *
     * Return the node of the heap whose key is minimal.
     *O(1)
     */
    public HeapNode findMin ()
    {
        return this.min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Meld the heap with heap2
     * O(1)
     */
    public void meld (FibonacciHeap heap2)
    {
        this.size+=heap2.size();
        this.marks+=heap2.marks;
        this.numOfTrees+=heap2.numOfTrees;
        if(this.size()!=0 &&heap2.size()!=0){
            HeapNode last=heap2.firstTree.prev;
            setSibling(this.firstTree.prev, heap2.firstTree);
            setSibling(last, this.firstTree);
        }
        else if(heap2.size()!=0){
            this.firstTree=heap2.firstTree;
        }
    }

    /**
     * public int size()
     *
     * Return the number of elements in the heap
     *O(1)
     */
    public int size ()
    {
        return this.size;
    }

    /**
     * public int[] countersRep()
     *
     * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap.
     *O(this.numOfTrees)
     */
    public int[] countersRep ()
    {
        int[] arr = new int[(int) Math.ceil(Math.log(size))+1];
        HeapNode node = firstTree;
        do {
            arr[node.rank]++;
            node = node.next;
        }while (node!=firstTree);
        return arr;
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     *O(n)
     */
    public void delete (HeapNode x)
    {
        changeKey(x, Integer.MIN_VALUE);
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * The function decreases the key of the node x by delta.
     * O(log n)
     */
    public void decreaseKey (HeapNode x,int delta)
    {
        x.key = x.key - delta;
        fixNodeAfterKeyChange(x);//cascading cuts if needed
    }
    /**
     * public void changeKey (HeapNode x,int key)
     *
     * The function changes the key of the node x to key.
     * O(log n)
     */
    public void changeKey (HeapNode x,int key){
        x.key = key;
        fixNodeAfterKeyChange(x);//cascading cuts if needed
    }

    /**
     * private void fixNodeAfterKeyChange (HeapNode x)
     *
     * The function changes the heap structure after changing the nodes key. performs the cascadingCuts procedure shown in lecture.
     * O(log n)
     */
    private void fixNodeAfterKeyChange (HeapNode x){
        if (!x.isARoot) {
            if (x.key < x.parent.key) {
                cut(x);
            }
        }
        if (x.key < this.min.key) {
            this.min = x;
        }

    }


    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     * O(1)
     */
    public int potential ()
    {
        return this.numOfTrees + 2 * this.marks;
    }


    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the run-time of the program.
     * O(1)
     */
    public static int totalLinks ()
    {
        return links;
    }
    /**
     * public HeapNode link (HeapNode first, HeapNode second)
     *
     * links the 2 arguments as shown in lecture.
     * O(1)
     */
    public HeapNode link (HeapNode first, HeapNode second){
        links++;
        numOfTrees--;
        first.prev=null;
        first.next=null;
        second.prev=null;
        second.next=null;
        if (first.key < second.key) {
            setAsChild(second, first);
            return first;
        } else {
            setAsChild(first, second);
            return second;
        }
    }
    /**
     * public void setAsChild (HeapNode child, HeapNode parent)
     *
     * sets the first argument as the child of the second, and connecting to the sibling linked list.
     * changes the child and parent field accordingly.
     * O(1)
     */
    public void setAsChild (HeapNode child, HeapNode parent){
        child.parent = parent;
        parent.rank++;
        child.isARoot = false;
        if (parent.rank > 1) {
            insertAsSibling(child, parent.child); //if parent has children, set as their sibling
            parent.child=child;
        }
        else{
            parent.child=child;
            child.prev=child;
            child.next=child;
        }

    }
    /**
     * public void insertAsSibling (HeapNode first, HeapNode second)
     *
     *connecting the first argument to the sibling linked list of the second argument
     * O(1)
     */
    public void insertAsSibling (HeapNode first, HeapNode second){
        if(second.prev==second){ //if second has no siblings
            second.prev=first;
            first.next=second;
            second.next=first;
            first.prev=second;
        }
        else{
            second.prev.next = first;
            first.prev = second.prev;
            first.next = second;
            second.prev = first;
        }

    }


    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the run-time of the program.
     * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts ()
    {
        return cuts;
    }
    /**
     * public void cut (HeapNode node)
     *
     * performs the cascadingCuts procedure shown in lecture.
     * O(log n)
     */
    public void cut (HeapNode node){
        cuts++;
        HeapNode parent = node.parent;
        parent.rank--;
        if(parent.rank==0){
            parent.child=null;
        }
        else{
            if(parent.child==node){
                parent.child=node.next;
            }
            setSibling(node.prev,node.next);
        }
        insertFirst(node);
        if(!parent.isARoot){
            if (!parent.mark) {
                parent.mark = true;
                marks++;
            } else {
                cut(parent);
            }
        }


    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k minimal elements in a binomial tree H.
     * O(k*deg(H)).
     */
    public static int[] kMin (FibonacciHeap H,int k)
    {
        Set<HeapNode> set=new HashSet<>();
        set.add(H.firstTree);
        int[] result=new int[k];
        int indexInArray=0;
        while(k-->0){
            HeapNode min=findMinimumOfSet(set);
            result[indexInArray++]=min.getKey();
            removeMinAndAddChildrenToSet(set,min);
        }
        return result;
    }
    /**
     * private static void removeMinAndAddChildrenToSet(Set<HeapNode> set, HeapNode minNode)
     *
     * removes the given node from set and adding its children to the set.
     * O(deg(H)).
     */
    private static void removeMinAndAddChildrenToSet(Set<HeapNode> set, HeapNode minNode) {
        set.remove(minNode);
        int numOfChildrenToAdd=minNode.rank;
        HeapNode child=minNode.child;
        while(numOfChildrenToAdd-->0){
            set.add(child);
            child=child.next;
        }
    }
    /**
     * private static HeapNode findMinimumOfSet(Set<HeapNode> set)
     *
     * returns the node with the minimum key from the set.
     * O(deg(H)).
     */

    private static HeapNode findMinimumOfSet(Set<HeapNode> set) {
        HeapNode minNode=null;
        for (HeapNode node:set){
            if (minNode==null||node.getKey()<minNode.getKey()){
                minNode=node;
            }
        }
        return minNode;
    }

    /**
     * public class HeapNode
     *
     */
    public class HeapNode {

        public int key;
        public HeapNode parent;
        public HeapNode child;
        public HeapNode prev;
        public HeapNode next;
        public int rank;
        public boolean mark = false;
        public boolean isARoot = true;


        public HeapNode(int key) {
            this.key = key;
        }

        public int getKey() {
            return this.key;
        }
    }
}
