package sero.util.queue;

public class ByteQueue implements Cloneable {
    private byte[] queue;
    private int head = -1;
    private int tail = 0;
    private int size = 0;

    private int markHead;
    private int markTail;
    private int markSize;

    public ByteQueue(){
        this(1024);
    }
    public ByteQueue(int initalLength){
        queue = new byte[initalLength];
    }
    public ByteQueue(byte[] b){
        this(b.length);
        push(b, 0, b.length);
    }
    public ByteQueue(byte[] b, int pos, int length){
        this(length);
        push(b, pos, length);
    }
    public ByteQueue(String hex){
        this(hex.length() / 2);
        push(hex);
    }
    public void push(String hex){
        if(hex.length() % 2 != 0){
            throw new IllegalArgumentException("Hex string must have an even number of characters");
        }
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        push(b, 0, b.length);
    }
    public void push(byte b){
        if(room() == 0){
            expand();
        }
        queue[tail] = b;
        if(head == -1){
            head = 0;
        }
        tail = (tail + 1) % queue.length;
        size++;
    }
    public void push(int i){
        push((byte) i);
    }
    public void push(long l){
        push((byte) l);
    }
    public void pushU2B(int i){
        push((byte)(i >> 8));
        push((byte) i);
    }
    public void pushU3B(int i){
        push((byte)(i >> 16));
        push((byte)(i >> 8));
        push((byte) i);
    }
    public void pushS4B(int i){
        pushInt(i);
    }
    public void pushU4B(long l){
        push((byte)(l >> 24));
        push((byte)(l >> 16));
        push((byte)(l >> 8));
        push((byte) l);
    }
    public void pushChar(char c){
        push((byte)(c >> 8));
        push((byte) c);
    }
    public void pushdouble(double d){
        pushLong(Double.doubleToLongBits(d));
    }
    public void pushFloat(float f){
        pushInt(Float.floatToIntBits(f));
    }
    public void pushInt(int i){
        push((byte)(i >> 24));
        push((byte)(i >> 16));
        push((byte)(i >> 8));
        push((byte) i);
    }
    public void pushLong(long l){
        push((byte)(l >> 56));
        push((byte)(l >> 48));
        push((byte)(l >> 40));
        push((byte)(l >> 32));
        push((byte)(l >> 24));
        push((byte)(l >> 16));
        push((byte)(l >> 8));
        push((byte) l);
    }
    public void pushShort(short s){
        push((byte)(s >> 8));
        push((byte) s);
    }
    public void read(InputStream in, int length) throws IOException{
        if(length == 0){
            return;
        }
        while(room() < length){
            expand();
        }
        int tailLength = queue.length - tail;
        if(tailLength > length){
            readImpl(in, tail, length);
        }else{
            readImpl(in, tail, tailLength);
        }
        if(length > tailLength){
            readImpl(in, 0, length - tailLength);
        }
        if(head == -1){
            head = 0;
        }
        tail = (tail + length) % queue.length;
        size += length;
    }
    private void readImple(InputStream in, int offset, int length) throws IOException{
        int readcount;
        while(length > 0){
            readcount = in.read(queue, offset, length);
            offset += readcount;
            length -= readcount;
        }
    }
    public void push(byte[] b){
        push(b, 0, b.length);
    }
    public void push(byte[]b, int pos, int length){
        if(length == 0){
            return;
        }
        while(room() < length){
            expand();
        }
        int tailLength = queue.length - tail;
        if(tailLength > length){
            System.arraycopy(b, pos, queue, tail, length);
        }else{
            System.arraycopy(b, pos, queue, tail, tailLength);
        }
        if(length > tailLength){
            System.arraycopy(b, tailLength + pos, queue, 0, length - tailLength);
        }
        if(head == -1){
            head = 0;
        }
        tail = (tail + length) % queue.length;
        size += length;
    }
    public void push(ByteQueue source){
        if(source.size == 0){
            return;
        }
        if(source == this){
            source = (ByteQueue) clone();
        }
        int firstCopyLen = source.queue.length - source.head;
        if(source.size < firstCopyLen){
            firstcopyLen = source.size;
        }
        push(source.queue, source.head, firstCopyLen);
        if(firstCopyLen < source.size){
            push(source.queue, 0, source.tail);
        }
    }
    public void push(ByteQueue source, int len){
        while(len-- > 0){
            push(source.pop());
        }
    }
    public void push(byteBuffer source){
        int length = source.remaining();
        if(length == 0){
            return;
        }
        while(room() < length){
            expand();
        }
        int tailLength = queue.length - tail;
        if(tailLength > length){
            source.get(queue, tail, length);
        }else{
            source.get(queue, tail, tailLength);
        }
        if(length > tailLength){
            source.get(queue, 0, length - tailLength);
        }
        if(head == -1){
            head = 0;
        }
        tail = (tail + length) % queue.length;
        size += length;
    }
    public void mark(){
        markHead = head;
        markTail = tail;
        markSize = size;
    }
    public void reset(){
        head = markHead;
        tail = markTail;
        size = markSize;
    }
    public byte pop(){
        byte retval = queue[head];

        if(size == 1){
            head = -1;
            tail = 0;
        }else{
            head = (head + 1) % queue.length;
        }
        size--;
        return retval;
    }
    public int popU1B(){
        return pop() & 0xff;
    }
    public int popU2B(){
        return ((pop() & 0xff) << 8) | (pop() & 0xff);
    }
    public int popU3B(){
        return ((pop() & 0xff) << 16) | ((pop() & 0xff) << 8) | (pop() & 0xff);
    }
    public short popS2B(){
        return (short) (((pop() & 0xff) << 8) | (pop() & 0xff));
    }
    public int popS4B(){
        return ((pop() & 0xff) << 24) | ((pop() & 0xff) << 16) | ((pop() & 0xff) << 8) | (pop() & 0xff);
    }
    public long popU4B(){
        return ((long)(pop() & 0xff) << 24) | ((long)(pop() & 0xff) << 16) | ((long)(pop() & 0xff) << 8) | (pop() & 0xff);
    }
    public int pop(byte[] buf){
        return pop(buf, 0 ,buf.length);
    }
    public int pop(byte[] buf, int pos, int length){
        length = peek(buf, pos, length);

        size -= length;

        if(size == 0){
            head = -1;
            tail = 0;
        }else{
            head = (head + length) % queue.length;
        }
        return length;
    }
    public int pop(int length){
        if(length == 0){
            return 0;
        }
        if(size == 0){
            throw new ArrayIndexOutOfBoundsException(-1);
        }
        if(length > size){
            length = size;
        }
        size -= length;

        if(size == 0){
            head = -1;
            tail = 0;
        }else{
            head = (head + length) % queue.length;
        }
        return length;
    }
    public String popString(int length, Charset charset){
        byte[] b = new byte[length];
        pop(b);
        return new String(b, charset);
    }
    public byte[] popAll(){
        byte[] data = new byte[size];
        pop(data, 0, data.length);
        return data;
    }
    public void write(OutputStream out) throws IOException{
        write(out, size);
    }
    public void write(OutputStream out, int length) throws IOException{
        if(length == 0){
            return;
        }
        if(size == 0){
            throw new ArrayIndexOutOfBoundsException(-1);
        }
    }

}
