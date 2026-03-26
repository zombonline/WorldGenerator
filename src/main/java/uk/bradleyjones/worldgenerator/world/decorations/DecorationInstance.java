package uk.bradleyjones.worldgenerator.world.decorations;

public class DecorationInstance {
    public Decoration decoration;
    public boolean enabled;

    public DecorationInstance(Decoration decoration, boolean enabled) {
        this.decoration = decoration;
        this.enabled = enabled;

    }
}