package com.kimi;

/**
 * @author 郭富城
 */
public class RBTree<K extends Comparable<K>, V> {


    /**
     * 根结点
     */
    TreeNode<K, V> root;


    /**
     * 插入方法
     */
    public void insert(K key, V value) {
        TreeNode<K, V> node = new TreeNode<K, V>(key, value, true);

        // 查找当前node的父节点
        TreeNode<K, V> parent = null;
        TreeNode<K, V> p = this.root;
        while (p!=null){
            parent = p;
            int cmp = node.key.compareTo(p.key);
            if (cmp>0){
                p = p.right;
            }else if(cmp<0){
                p = p.left;
            }else {
                p.setValue(node.value);
                return;
            }
        }

        node.parent = parent;
        if (parent!=null){
            int cmp = node.key.compareTo(parent.key);
            if (cmp>0){
                parent.right = node;
            }else{
                parent.left = node;
            }
        }else {
            node.setRed(false);
            node.setParent(null);
            root = node;
        }

        // 插入后要重新平衡
        balanceInsertion(node);
    }

    /**
     * 插入后重新平衡红黑树
     *
     *      1. 情景1： 红黑树为空树,将颜色改为黑色
     *      2. 情景2： 插入结点的key存在，不用处理
     *      3. 情景3： 插入结点的父节点为黑色，不用处理
     *
     *
     *      4. 情景4： 插入结点的父节点为红色
     *          4.1： 叔叔结点存在，且为红色 --》父-叔 都为红，  将爸爸结点和叔叔结点染色为黑色，将爷爷结点染色为红色，并且再以爷爷结点为当前结点进行下一轮处理
     *          4.2： 叔叔结点不存在，或者为黑色,父结点为爷爷结点的左子树
     *              4.2.1：插入结点为其父节点的左结点 （LL）：
     *                        将爸爸结点染色为黑色，将爷爷结点染色为红色，然后以爷爷结点右旋
     *                        pp(B)                              pp(R)
     *                        /  \                               /  \
     *                     p(R)  NIL或为黑色    ==>            p(B)  NIL或为黑色  以爷爷右旋
     *                    /                                 /
     *          insertNode(R)                      insertNode(R)
     *
     *              4.2.2：插入结点为其父节点的右结点（LR）：
     *                    以爸爸结点进行一次左旋，得到情景（4.2.1），然后指定爸爸结点为当前结点进行处理
     *                        pp(B)
     *                        /  \
     *                     p(R)  NIL或为黑色
     *                        \
     *                        insertNode(R)
     *
     *          4.3：叔叔结点不存在，或者为黑色,父结点为爷爷结点的右子树：
     *              4.3.1：插入结点为其父节点的右结点 （RR）
     *                     将爸爸染色为黑色，将爷爷染色为红色，然后以爷爷结点左旋
     *              4.3.2：插入结点为其父节点的左结点 （RL）
     *                     以爸爸结点进行一次右旋，得到情景（4.3.2）然后指定爸爸结点为当前结点进行下一轮处理
     */
    private void balanceInsertion(TreeNode<K,V> node){
        // 情景1：
        this.root.setRed(false);

        // 情景4：插入结点的父亲结点是红色
        TreeNode<K,V> p = node.parent;

        if (p!=null&&p.isRed()){
            TreeNode<K,V> pp = p.parent;
            TreeNode<K,V> uncle = null;
            // 父节点为爷爷结点的左子树
            if (p == pp.left){
                uncle = pp.right;
                // 情景4.1：叔叔结点存在，且为红色
                if (uncle!=null&&uncle.isRed()){
                    // 将爸爸和叔叔染为黑色，将爷爷染为红色，再以爷爷结点为当前结点进行递归
                    p.setRed(false);
                    uncle.setRed(false);
                    pp.setRed(true);
                    balanceInsertion(pp);
                }
                // 情景4.2 叔叔结点不存在，或者为黑色
                else if (uncle==null||!uncle.isRed()){
                    // 情景4.2.1：插入结点为其父节点的左结点（LL）：
                    if (node == p.left){
                        p.setRed(false);
                        pp.setRed(true);
                        rotateRight(pp);
                    }
                    // 情景4.2.2：插入结点为其父结点的右结点（LR）：
                    else if (node == p.right){
                        rotateLeft(p);
                        balanceInsertion(p);
                    }
                }
            }
            // 父结点为爷爷结点的右子树
            else if (p == pp.right){
                uncle = pp.left;
                // 情景4.1：叔叔结点存在，且为红色
                if (uncle!=null&&uncle.isRed()){
                    // 将爸爸和叔叔染为黑色，将爷爷染为红色，再以爷爷结点为当前结点进行递归
                    p.setRed(false);
                    uncle.setRed(false);
                    pp.setRed(true);
                    balanceInsertion(pp);
                }
                // 情景4.3 叔叔结点不存在，或者为黑色
                else if (uncle==null||!uncle.isRed()){
                    // 情景4.3.1：插入结点为其父节点的右结点（RR）：
                    if (node == p.right){
                        p.setRed(false);
                        pp.setRed(true);
                        rotateLeft(pp);
                    }
                    // 情景4.3.2：插入结点为其父结点的右结点（RL）：
                    else if (node == p.left){
                        rotateRight(p);
                        balanceInsertion(p);
                    }
                }
            }
        }
    }


    /**
     * 删除
     * @param key 键
     * @return 删除的value
     */
    public V remove(K key){
        TreeNode<K,V> node = find(key);
        if (node==null){
            return null;
        }
        V oldValue = node.value;
        removeTreeNode(node);
        return oldValue;
    }

    /**
     * 删除红黑树中结点（红黑树的删除，最终肯定删除对应2-3-4树的叶子结点
     *      1. 情景1：删除叶子结点，直接删除
     *              1.1：删除的结点为黑色，先平衡再删除
     *      2. 情景2：删除结点有一个子结点，用子节点来替代
     *              2.1：删除的结点为根结点，根结点指向替换结点
     *              2.2：删除结点为黑色，平衡
     *      3. 情景3：删除的结点有两个子结点，此时我们需要获取对应的前驱或后继结点来替代、将能够转换为情景1或情景2
     * @param node 结点
     */
    private void removeTreeNode(TreeNode<K, V> node) {
        // 情景3：转换为情景1或情景2
        while (node.left!=null&&node.right!=null){
            // 查找后继结点
            TreeNode<K, V> successor = successor(node);
            node.key = successor.key;
            node.value = successor.value;
            node = successor;
        }
        // 获取需要替换的结点
        TreeNode<K, V> replacement = node.left==null?node.right:node.left;
        // 情况2：有一个子结点，直接拿来顶
        if (replacement !=null){
            replacement.parent = node.parent;
            if (node.parent == null){
                root = replacement;
            }else if (node == node.parent.left){
                node.parent.left = replacement;
            }else if (node == node.parent.right){
                node.parent.right = replacement;
            }
            // 将node引用置为空，等待gc
            node.left = node.right = node.parent = null;
            if (!node.isRed()){
                balanceDeletion(replacement);
            }
        }
        // 删除的是root结点情况
        else if (node.parent == null){
            this.root = null;
        }
        // 情况1：没有子结点
        else{
            // 先平衡
            if (!node.isRed()){
                balanceDeletion(node);
            }
            // 再删除
            if (node.parent !=null){
                if (node.parent.left == node){
                    node.parent.left = null;
                }else{
                    node.parent.right = null;
                }
            }
        }
    }

    /**
     * 结点删除时进行调整
     * 2-3-4树删除操作
     *      删除的是3、或4结点可以直接删除，删除的是2结点，整颗红黑树就不平衡了
     *      1.情况1：自己能处理，对应叶子结点是3结点或4结点
     *      2.情况2：自己处理不了需要兄弟结点借，父亲下来，然后兄弟结点找一个人替父亲当家
     *                   p                         pl2
     *                 /  \          ====>        /  \
     *          pl1,pl2   pl3(remove,b)         pl1   p
     *      3.情况3：跟兄弟借，兄弟借不了
     *                 p(r)                          p(b)
     *                /  \          ====>               \
     *  remove-> pl1(b)  pl2(b)                         pl2(r)
     * @param node 结点
     */
    private void balanceDeletion(TreeNode<K,V> node){
        // 情况2和3
        while (node!=root&&!node.isRed()){
            if(node==node.parent.left){
                // 查找兄弟结点
                TreeNode<K, V> bro = node.parent.right;
                if (bro.isRed()){
                    // 如果是红色，不是真兄弟，因为在红黑树对应的2-3-4树里红色结点不会单独作为结点
                    //              p(b)                    bro(b)
                    //              /  \                    /  \
                    //         node(b)  bro(r)  =====>    p(r)  prr(b)
                    //                                   /   \
                    //                             node(b)   prl(b)
                    bro.setRed(false);
                    node.parent.setRed(true);
                    rotateLeft(node.parent);
                    bro = node.parent.right;
                }
                if (!bro.left.isRed()&&!bro.right.isRed()){
                    // 情况3：兄弟结点的两个孩子都为黑，没得借
                    bro.setRed(true);
                    node = node.parent;
                }else {
                    // 情况2： 跟兄弟借，兄弟有的借
                    if (!bro.right.isRed()){
                        // 不存在右孩子，那么肯定存在左孩子
                        //              p(r)                               p(r)
                        //              /  \                               /  \
                        //         node(b)  bro(b)           =====>   node(b)  brol(b)
                        //                 /   \                              /   \
                        //           brol()     bror(b)                 broll()     bro(r)
                        bro.setRed(true);
                        bro.left.setRed(false);
                        rotateRight(bro);
                        bro = node.parent.right;
                    }
                    //             p(r)                          p(b)                            bro(r)
                    //             /  \                          /  \                            /  \
                    //        node(b)  bro(b)        ====>  node(b)  bro(r)      ====>        p(b)  bror(b)
                    //                /   \                         /   \                    /   \
                    //          brol()     bror(r)            brol()     bror(b)        node(b)    brol()
                    bro.setRed(node.parent.red);
                    node.parent.setRed(false);
                    bro.right.setRed(false);
                    rotateLeft(node.parent);
                    node = root;
                }
            }else {
                // 查找兄弟结点
                TreeNode<K, V> bro = node.parent.left;
                if (bro.isRed()){
                    bro.setRed(false);
                    node.parent.setRed(true);
                    rotateRight(node.parent);
                    bro = node.parent.left;
                }
                if (!bro.right.isRed()&&!bro.left.isRed()){
                    // 情况3：兄弟结点的两个孩子都为黑，没得借
                    bro.setRed(true);
                    node = node.parent;
                }else {
                    // 情况2： 跟兄弟借，兄弟有的借
                    if (!bro.left.isRed()){
                        // 不存在右孩子，那么肯定存在左孩子
                        //              p(r)                               p(r)
                        //              /  \                               /  \
                        //         node(b)  bro(b)           =====>   node(b)  brol(b)
                        //                 /   \                              /   \
                        //           brol()     bror(b)                 broll()     bro(r)
                        bro.setRed(true);
                        bro.right.setRed(false);
                        rotateLeft(bro);
                        bro = node.parent.left;
                    }
                    //             p(r)                          p(b)                            bro(r)
                    //             /  \                          /  \                            /  \
                    //        node(b)  bro(b)        ====>  node(b)  bro(r)      ====>        p(b)  bror(b)
                    //                /   \                         /   \                    /   \
                    //          brol()     bror(r)            brol()     bror(b)        node(b)    brol()
                    bro.setRed(node.parent.red);
                    node.parent.setRed(false);
                    bro.left.setRed(false);
                    rotateRight(node.parent);
                    node = root;
                }
            }
        }
        // 情况1：替代的结点是红色，直接设置为黑色即可
        node.setRed(false);
    }

    /**
     * 查找前驱结点
     * @param node 当前结点
     * @return 前驱结点
     */
    private TreeNode<K,V> predecessor(TreeNode<K,V> node){
        if (node==null) {
            return null;
        }
        else if (node.left!=null){
            // 拥有左孩子的情况
            TreeNode<K,V> s = node.left;
            while (s.right!=null){
                s = s.right;
            }
            return s;
        }
        // 没有左孩子的情况，在删除情况下不存在
        else {
            TreeNode<K,V> p = node.parent;
            TreeNode<K,V> s = node;
            while (p!=null&&s==p.left){
                 s = p;
                 p = p.parent;
            }
            return p;
        }
    }

    /**
     * 查找后继结点
     * @param node 当前结点
     * @return 前驱结点
     */
    private TreeNode<K,V> successor(TreeNode<K,V> node){
        if (node==null) {
            return null;
        }
        else if (node.right!=null){
            // 拥有右孩子的情况
            TreeNode<K,V> s = node.right;
            while (s.left!=null){
                s = s.left;
            }
            return s;
        }
        // 没有右孩子的情况，在删除情况下不存在
        else {
            TreeNode<K,V> p = node.parent;
            TreeNode<K,V> s = node;
            while (p!=null&&s==p.right){
                s = p;
                p = p.parent;
            }
            return p;
        }
    }


    /**
     * 查找结点
     * @param key 结点键值
     * @return 结点
     */
    private TreeNode<K,V> find(K key){
        TreeNode<K,V> node = this.root;
        while (node!=null){
            int cmp = key.compareTo(node.key);
            if (cmp<0){
                node = node.left;
            }else if(cmp>0){
                node = node.right;
            }else {
                return node;
            }
        }
        return null;
    }



    /**
     * 左旋
     *           p               pr
     *          / \              /\
     *        pl  pr  ===>      p  prr
     *            / \          / \
     *          prl  prr     pl  prl
     * <p>
     * 左旋时：p-pl 和 pr-prr不变
     * pr-prl 变为 pr-p
     * <p>
     * 步骤：
     * 1. 判断p结点是否有父结点
     * 1. 如果没有 pr为root结点
     * 2. 如果有 pr.parent = p.parent,并设置pr的父节点为p的父节点的左孩子或右孩子p.parent.left==p?pr.parent.left = pr:pr.parent.right = pr;
     *
     * @param p 需左旋的结点
     */
    private void rotateLeft(TreeNode<K, V> p) {
        if (p != null) {
            TreeNode<K, V> pr = p.right;
            TreeNode<K, V> prl = p.right = pr.left;
            if (prl != null) {
                pr.left.parent = p;
            }
            pr.parent = p.parent;
            // 判断p是否有父节点
            if (p.parent == null) {
                root = pr;
            }
            // 判断是左结点还是右结点
            else if (p.parent.left == p) {
                p.parent.left = pr;
            } else {
                p.parent.right = pr;
            }
            // 最后设置 p 为 pr 的左结点
            pr.left = p;
            p.parent = pr;
        }
    }

    /**
     * 右旋
     *         p                pl
     *        / \              / \
     *       pl  pr  ===>    pll  p
     *      / \                  / \
     *      pll  plr             plr  pr
     * <p>
     * 步骤类似左旋
     *
     * @param p 需右旋的结点
     */

    private void rotateRight(TreeNode<K, V> p) {
        if (p != null) {
            TreeNode<K, V> pl = p.left;
            TreeNode<K, V> plr = p.left = pl.right;
            if (plr != null) {
                pl.right.parent = p;
            }
            pl.parent = p.parent;
            // 判断p是否有父节点
            if (p.parent == null) {
                root = pl;
            }
            // 判断是左结点还是右结点
            else if (p.parent.left == p) {
                p.parent.left = pl;
            } else {
                p.parent.right = pl;
            }
            // 最后设置 p 为 pr 的左结点
            pl.right = p;
            p.parent = pl;
        }
    }

    static class TreeNode<K extends Comparable<K>, V> {
        TreeNode<K, V> parent;
        TreeNode<K, V> left;
        TreeNode<K, V> right;
        boolean red;
        K key;
        V value;


        public TreeNode(TreeNode<K, V> parent, K key, V value) {
            this.parent = parent;
            this.key = key;
            this.value = value;
        }

        public TreeNode(K key, V value, boolean red) {
            this.red = red;
            this.key = key;
            this.value = value;
        }

        public TreeNode<K, V> getParent() {
            return parent;
        }

        public void setParent(TreeNode<K, V> parent) {
            this.parent = parent;
        }

        public TreeNode<K, V> getLeft() {
            return left;
        }

        public void setLeft(TreeNode<K, V> left) {
            this.left = left;
        }

        public TreeNode<K, V> getRight() {
            return right;
        }

        public void setRight(TreeNode<K, V> right) {
            this.right = right;
        }

        public boolean isRed() {
            return red;
        }

        public void setRed(boolean red) {
            this.red = red;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }

}
