package gc;

/**
 * For simplicity, implement Fenichel's algorithm instead of Cheney's algorithm.
 * 
 * Semi-space garbage collection [Fenichel, 1969] is a copying algorithm, which
 * means that reachable objects are relocated from one address to another during
 * a collection. Available memory is divided into two equal-size regions called
 * "from-space" and "to-space".
 * 
 * Allocation is simply a matter of keeping a pointer into to-space which is
 * incremented by the amount of memory requested for each allocation (that is,
 * memory is allocated sequentially out of to-space). When there is insufficient
 * space in to-space to fulfill an allocation, a collection is performed.
 * 
 * A collection consists of swapping the roles of the regions, and copying the
 * live objects from from-space to to-space, leaving a block of free space
 * (corresponding to the memory used by all unreachable objects) at the end of
 * the to-space.
 * 
 * Since objects are moved during a collection, the addresses of all references
 * must be updated. This is done by storing a "forwarding address" for an object
 * when it is copied out of from-space. Like the mark-bit, this forwarding
 * address can be thought of as an additional field of the object, but is
 * usually implemented by temporarily repurposing some space from the object.
 * 
 * The primary benefits of semi-space collection over mark-sweep are that the
 * allocation costs are extremely low (no need to maintain and search the free
 * list), and fragmentation is avoided.
 */
public class CopyCollectHeap extends Heap {
	private static final int SIZE = -1;
	private static final int FORWARD = -2;

	private int toSpace;
	private int fromSpace;
	private int allocPtr;

	/**
	 * Though the super constructor is invoked and the free list is initialized,
	 * the free list is not used in the implementation of this copy collector.
	 */
	public CopyCollectHeap(int size) {
		super(size);
		toSpace = 0;
		fromSpace = size / 2;
		allocPtr = toSpace;
	}

	public void allocate(Var v, int size) throws InsufficientMemory {
		// TODO
		try {
			if (Math.abs(fromSpace-toSpace) < allocPtr-toSpace + size + 2) throw new InsufficientMemory();
			v.addr = allocPtr + 2;
			data[v.addr+SIZE] = size;
			data[v.addr+FORWARD] = -1;
			for(int i=v.addr; i<v.addr+size; ++i) data[i] = -1;
			allocPtr += (size+2);
		}catch(InsufficientMemory e){
			collect();
			if (Math.abs(fromSpace-toSpace) < allocPtr-toSpace + size + 2) throw new InsufficientMemory();
			v.addr = allocPtr + 2;
			data[v.addr+SIZE] = size;
			data[v.addr+FORWARD] = -1;
			for(int i=v.addr; i<v.addr+size; ++i) data[i] = -1;
			allocPtr += (size+2);
		}
	}

	private void collect() {
		// TODO

		allocPtr = fromSpace;
		for(Var var : reachable){
			if(!var.isNull())
				var.addr = copy(var.addr);
		}

		int temp = toSpace;
		toSpace = fromSpace;
		fromSpace = temp;
	}

	private int copy(int addr) {
		// TODO
		if(data[addr+FORWARD]!=-1) return data[addr+FORWARD];

		int temp = allocPtr+2;
		data[addr+FORWARD] = allocPtr+2;
		allocPtr += (data[addr+SIZE]+2);
		for(int i = temp; i < temp + data[addr+SIZE]; ++i){
			if(data[addr+i-temp]<0) {
				data[i] = data[addr+i-temp];
			}
			else {
				data[i] = copy(data[addr+i-temp]);
			}
		}
		data[temp+SIZE] = data[addr+SIZE];
		data[temp+FORWARD] = -1;

		return data[addr+FORWARD];
	}
}
