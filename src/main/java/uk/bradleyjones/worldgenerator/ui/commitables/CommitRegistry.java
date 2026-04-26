package uk.bradleyjones.worldgenerator.ui.commitables;

import java.util.ArrayList;
import java.util.List;

public class CommitRegistry {
    private static final List<Commitable> commitables = new ArrayList<>();

    public static void register(Commitable c) {
        commitables.add(c);
    }

    public static void unregister(Commitable c) {
        commitables.remove(c);
    }

    public static void commitAll() {
        commitables.forEach(Commitable::commit);
    }
}