package com.cyclic.models.game.net.toclient.broadcast;

import com.cyclic.configs.Enums;
import com.cyclic.models.game.Node;
import com.cyclic.models.game.net.toclient.NodesLink;
import com.cyclic.models.game.net.toclient.PlayerScore;
import com.cyclic.models.game.net.toclient.RNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

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
    private ArrayList<PlayerScore> scores;
    private HashSet<Node> valueUpdate;
    private HashSet<Node> newNodes;
    private HashSet<RNode> removedNodes;
    private HashSet<NodesLink> newLinks;
    private HashSet<NodesLink> removedLinks;

    public void addOtherMoveBroadcast(MoveBroadcast moveBroadcast) {
        if (moveBroadcast.getValueUpdate() != null) {
            valueUpdate = new HashSet<>();
            valueUpdate.addAll(moveBroadcast.getValueUpdate());
        }

        if (moveBroadcast.getNewNodes() != null) {
            newNodes = new HashSet<>();
            newNodes.addAll(moveBroadcast.getNewNodes());
        }

        if (moveBroadcast.getRemovedNodes() != null) {
            removedNodes = new HashSet<>();
            removedNodes.addAll(moveBroadcast.getRemovedNodes());
        }

        if (moveBroadcast.getNewLinks() != null) {
            newLinks = new HashSet<>();
            newLinks.addAll(moveBroadcast.getNewLinks());
        }

        if (moveBroadcast.getRemovedLinks() != null) {
            removedLinks = new HashSet<>();
            removedLinks.addAll(moveBroadcast.getRemovedLinks());
        }
    }

    public void addScores(PlayerScore score) {
        if (scores == null)
            scores = new ArrayList<>();
        scores.add(score);
    }

    public void addValueUpdate(Node node) {
        if (valueUpdate == null)
            valueUpdate = new HashSet<>();
        valueUpdate.add(node);
    }

    public void addNewNode(Node node) {
        if (newNodes == null)
            newNodes = new HashSet<>();
        newNodes.add(node);
    }

    public void addRemovedNode(RNode node) {
        if (removedNodes == null)
            removedNodes = new HashSet<>();
        removedNodes.add(node);
    }

    public void addNewLink(NodesLink link) {
        if (newLinks == null)
            newLinks = new HashSet<>();
        newLinks.add(link);
    }

    public void addNewLink(Node l, Node r) {
        addNewLink(new NodesLink(l.getReduced(), r.getReduced()));
    }

    public void addRemovedLink(NodesLink link) {
        if (removedLinks == null)
            removedLinks = new HashSet<>();
        removedLinks.add(link);
    }

    public void sortScores() {
        if (scores != null) {
            scores.sort((o1, o2) -> (int) (o2.getScore() - o1.getScore()));
        }
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

    public Enums.MoveResult getResult() {
        return result;
    }

    public void setResult(Enums.MoveResult result) {
        this.result = result;
    }

    public HashSet<Node> getValueUpdate() {
        return valueUpdate;
    }

    public HashSet<Node> getNewNodes() {
        return newNodes;
    }

    public HashSet<RNode> getRemovedNodes() {
        return removedNodes;
    }

    public void setRemovedNodes(HashSet<RNode> removedNodes) {
        this.removedNodes = removedNodes;
    }

    public HashSet<NodesLink> getNewLinks() {
        return newLinks;
    }

    public HashSet<NodesLink> getRemovedLinks() {
        return removedLinks;
    }

    public void setRemovedLinks(HashSet<NodesLink> removedLinks) {
        this.removedLinks = removedLinks;
    }
}
