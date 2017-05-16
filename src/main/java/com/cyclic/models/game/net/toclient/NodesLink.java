package com.cyclic.models.game.net.toclient;

import com.cyclic.models.game.Node;

/**
 * Created by serych on 15.05.17.
 */
public class NodesLink {
    private RNode l;
    private RNode r;

    public NodesLink(RNode l, RNode r) {
        this.l = l;
        this.r = r;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass())
            return false;
        NodesLink nodesLink = (NodesLink) obj;
        return (nodesLink.l.equals(l) && nodesLink.r.equals(r)) ||
                (nodesLink.r.equals(l) && nodesLink.l.equals(r));
    }

    @Override
    public int hashCode() {
        return l.hashCode() * 100 + r.hashCode();
    }
}
