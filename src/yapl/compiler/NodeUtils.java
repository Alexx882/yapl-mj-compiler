package yapl.compiler;

public final class NodeUtils {

    private NodeUtils() {
    }

    /**
     * Tests if the given node has any children of the given type.
     *
     * @param parent        the node to check
     * @param cls           the type to look for
     * @param maxRecursions maximum number of recursions. 0: no recursion, -1: no limit
     * @param <N>           the type to look for
     * @return true iff there is a node of type cls at most maxRecursions levels below parent in the tree
     */
    public static <N extends Node> boolean hasChildOfType(Node parent, Class<N> cls, int maxRecursions) {
        return getFirstChildOfType(parent, cls, maxRecursions) != null;
    }

    /**
     * Returns the first child of the given type.
     *
     * @param parent        the node to check
     * @param cls           the type to look for
     * @param maxRecursions maximum number of recursions. 0: no recursion, -1: no limit
     * @param <N>           the type to look for
     * @return a node of type N or null, if none was found
     */
    public static <N extends Node> N getFirstChildOfType(Node parent, Class<N> cls, int maxRecursions) {
//        if (maxRecursions < -1) return false;

        int nChildren = parent.jjtGetNumChildren();
        for (int i = 0; i < nChildren; i++) {

            Node child = parent.jjtGetChild(i);

            if (cls.isInstance(child)) {
                return cls.cast(child);

            } else if (maxRecursions != 0) {
                var result = getFirstChildOfType(child, cls, maxRecursions > 0 ? maxRecursions - 1 : -1);
                if (result != null)
                    return cls.cast(result);
            }
        }

        return null;
    }
}
