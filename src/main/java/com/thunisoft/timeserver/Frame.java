package com.thunisoft.timeserver;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Frame {
    private static final long serialVersionUID = 1L;

    public int seqNo;
    public Date date = new Timestamp(System.currentTimeMillis());
    public int length;
    
    public ByteBuf encode() {
        
        ByteBuf out = Unpooled.buffer(1440);
        
        out.writeByte('$');
        out.writeInt(seqNo);
        out.writeLong(date.getTime());
        out.writeShort(out.writableBytes() - 2);
        out.writerIndex(out.capacity());

        return out;
    }

    public static Frame decode(ByteBuf in) {
        in.markReaderIndex();
        
        try {
            char ch = (char) in.readByte();
            if (ch != '$') {
                return null;
            }
            
            Frame frame = new Frame();
            frame.seqNo = in.readInt();
            frame.date = new Timestamp(in.readLong());
            frame.length = in.readShort();
            
            int remains = frame.length;
            while(remains > 0) {
                in.readByte();
                remains --;
            }

            return frame;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(seqNo).append(".");
        buf.append(", length=").append(length);
        buf.append(", t=").append(new SimpleDateFormat("HH:mm:ss.SSS").format(date));

        return buf.toString();
    }
}
