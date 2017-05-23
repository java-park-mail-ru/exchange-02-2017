package com.cyclic.models.game.net.toclient.broadcast;

import com.cyclic.configs.Enums;
import com.cyclic.models.game.Node;
import com.cyclic.models.game.net.toclient.NodesLink;
import com.cyclic.models.game.net.toclient.PlayerScore;
import com.cyclic.models.game.net.toclient.RNode;

import java.util.Comparator;
import java.util.Vector;

import static com.cyclic.configs.Enums.Datatype.DATATYPE_PLAYERMOVE;

/**
 * Created by serych on 15.05.17.
 */
public class MoveBroadcast {

    private final Enums.Datatype datatype = DATATYPE_PLAYERMOVE;
    private long pid;
    private long nextpid;
    private Long deadpid = null;
    private Enums.MoveResult result;
    private Vector<PlayerScore> scores;
    private Vector<Node> valueUpdate;
    private Vector<Node> newNodes;
    private Vector<RNode> removedNodes;
    private Vector<NodesLink> newLinks;
    private Vector<NodesLink> removedLinks;

    public void addScores(PlayerScore score) {
        if (scores == null)
            scores = new Vector<>();
        scores.add(score);
    }

    public void addValueUpdate(Node node) {
        if (valueUpdate == null)
            valueUpdate = new Vector<>();
        valueUpdate.add(node);
    }

    public void addNewNode(Node node) {
        if (newNodes == null)
            newNodes = new Vector<>();
        newNodes.add(node);
    }

    public void addRemovedNode(RNode node) {
        if (removedNodes == null)
            removedNodes = new Vector<>();
        removedNodes.add(node);
    }

    public void addNewLink(NodesLink link) {
        if (newLinks == null)
            newLinks = new Vector<>();
        newLinks.add(link);
    }

    public void addNewLink(Node l, Node r) {
        addNewLink(new NodesLink(l.getReduced(), r.getReduced()));
    }

    public void addRemovedLink(NodesLink link) {
        if (removedLinks == null)
            removedLinks = new Vector<>();
        removedLinks.add(link);
    }

    public void sortScores() {
        scores.sort(new Comparator<PlayerScore>() {
            @Override
            public int compare(PlayerScore o1, PlayerScore o2) {
                return (int) (o2.getScore() - o1.getScore());
            }
        });
    }

    public Long getDeadpid() {
        return deadpid;
    }

    public void setDeadpid(Long deadpid) {
        this.deadpid = deadpid;
    }

    public void addRemovedLink(Node l, Node r) {
        addRemovedLink(new NodesLink(l.getReduced(), r.getReduced()));
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public void setNextpid(long nextpid) {
        this.nextpid = nextpid;
    }

    public void setResult(Enums.MoveResult result) {
        this.result = result;
    }

    public void setRemovedNodes(Vector<RNode> removedNodes) {
        this.removedNodes = removedNodes;
    }

    public void setRemovedLinks(Vector<NodesLink> removedLinks) {
        this.removedLinks = removedLinks;
    }

    public Enums.MoveResult getResult() {
        return result;
    }
}
