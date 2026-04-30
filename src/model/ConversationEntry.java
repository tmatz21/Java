package model;

public class ConversationEntry {
    public final int conversationId;
    public final int otherUserId;
    public final String label;
    public final int unread;

    public ConversationEntry(int conversationId, int otherUserId, String label, int unread) {
        this.conversationId = conversationId;
        this.otherUserId = otherUserId;
        this.label = label;
        this.unread = unread;
    }

    @Override
    public String toString() { return label; }
}