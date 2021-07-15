package me.gaegul.ch19;

public class PersistentTree {
    public static void main(String[] args) {
        Tree t = new Tree("Mary", 22,
                new Tree("Emily", 20,
                        new Tree("Alan", 50, null, null),
                        new Tree("Georgie", 23, null, null)
                ),
                new Tree("Tian", 29,
                        new Tree("Raoul", 23, null, null),
                        null
                )
        );

        // 발견 = 23
        System.out.printf("Raoul: %d%n", lookup("Raoul", -1, t));
        // 발견되지 않음 = -1
        System.out.printf("Jeff: %d%n", lookup("Jeff", -1, t));

        Tree f = fupdate("Jeff", 80, t);
        // 발견 = 80
        System.out.printf("Jeff: %d%n", lookup("Jeff", -1, f));

        Tree u = update("Jim", 40, t);
        // fupdate로 t가 바뀌지 않았으므로 Jeff는 발견되지 않음 = -1
        System.out.printf("Jeff: %d%n", lookup("Jeff", -1, u));
        // 발견 = 40
        System.out.printf("Jim: %d%n", lookup("Jim", -1, u));

        Tree f2 = fupdate("Jeff", 80, t);
        // 발견 = 80
        System.out.printf("Jeff: %d%n", lookup("Jeff", -1, f2));
        // t로 만든 f2는 위 update()에서 갱신되므로 Jim은 여전히 존재함 = 40
        System.out.printf("Jim: %d%n", lookup("Jim", -1, f2));
    }

    public static class Tree {
        private String key;
        private int val;
        private Tree left;
        private Tree right;

        public Tree(String key, int val, Tree left, Tree right) {
            this.key = key;
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public static int lookup(String key, int defaultVal, Tree tree) {
        if (tree == null) return defaultVal;
        if (key.equals(tree.key)) return tree.val;
        return lookup(key, defaultVal, key.compareTo(tree.key) < 0 ? tree.left : tree.right);
    }

    public static Tree update(String key, int newVal, Tree tree) {
        if (tree == null) tree = new Tree(key, newVal, null, null);
        else if (key.equals(tree.key)) tree.val = newVal;
        else update(key, newVal, key.compareTo(tree.key) < 0 ? tree.left : tree.right);
        return tree;
    }

    public static Tree fupdate(String key, int newVal, Tree tree) {
        return (tree == null) ?
                new Tree(key, newVal, null, null) :
                    key.equals(tree.key) ?
                            new Tree(key, newVal, tree.left, tree.right) :
                    key.compareTo(tree.key) < 0 ?
                            new Tree(tree.key, tree.val, fupdate(key, newVal, tree.left), tree.right) :
                            new Tree(tree.key, tree.val, tree.left, fupdate(key, newVal, tree.right));
    }
}
