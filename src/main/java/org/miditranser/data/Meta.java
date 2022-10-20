package org.miditranser.data;

import org.miditranser.data.midi.message.MetaMessage;

import static org.miditranser.Utils.byteArrayToInt;
import static org.miditranser.Utils.tempo2bpm;

public class Meta implements Addable {

    public byte getType() {
        return type;
    }

    private final byte type;

    public long getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }

    private final long length;

    private final byte[] data;

    public Meta(byte type, long length, byte[] data) {
        this.type = type;
        this.length = length;
        this.data = data;
    }

    public Meta(MetaMessage meta) {
        this(meta.getType(), meta.getLength(), meta.getData());
    }

    public boolean isChangBpm() {
        return getType() == 0x51;
    }

    public static int bytesToBpm(byte[] data) {
        return tempo2bpm(byteArrayToInt(data));
    }

    @Override
    public String generateMiderCode(CalculateDurationConfiguration cdc) {
        return "";
    }
}
