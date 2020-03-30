package yapl.impl;

public enum OperandType {
    s8(1),
    s16(2),
    s32(4);

    int size;

    OperandType(int size) {
        this.size = size;
    }
}
