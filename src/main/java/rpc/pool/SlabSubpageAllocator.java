package rpc.pool;

public class SlabSubpageAllocator {
    private int elemAmount;
    private int bitMapLength;
    private long[] bitMap; // 0 - idle, 1 - used

    public SlabSubpageAllocator(int elemAmount) {
        assert elemAmount > 0;
        this.elemAmount = elemAmount;
        bitMapLength = (elemAmount + 63) >>> 6;
        bitMap = new long[bitMapLength];
    }

    int obtainIdelPosition() {
        for (int i = 0; i < bitMapLength; i++) {
            if (~bitMap[i] != 0) {
                int pos = obtainIdelPosition(bitMap[i]);
                if (pos == -1) {
                    return -1;
                }
                bitMap[i] |= (1L << pos); // set idle state to used
                return (i << 6) + pos;
            }
        }
        return -1;
    }

    private int obtainIdelPosition(long state) {
        for (int j = 0; j < 64; j++) {
            if ((state & 1) == 0) {
                return j;
            }
            state >>>= 1;
        }
        return -1;
    }

    public boolean free(int pos) {
        assert (pos >= 0) && (pos < elemAmount);
        int i = pos >>> 6;
        int j = pos % 64;
        if (((bitMap[i] >> j) & 1L) == 0) {
            return false;
        }
        bitMap[i] &= (~(1L << j));
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elemAmount; i++) {
            int j = i >>> 6;
            int k = i % 64;
            sb.append((bitMap[j] >> k) & 1L).append(' ');
        }
        return sb.toString();
    }
}
