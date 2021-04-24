package gc;

import java.util.ArrayList;

/**
 * A reference-counting heap.
 */
public class RefCountHeap extends Heap {
	private static final int SIZE = -1;
	private static final int COUNTER = -2;

	public RefCountHeap(int size) {
		super(size);
	}

	public void endScope() {
		// TODO decrease counters
		ArrayList<Var> l = (ArrayList<Var>)currentScope();

		for (Var var : l) {
			decreaseCounter(var.addr);
		}

		super.endScope();
	}

	/**
	 * Allocate memory with 2 extra slots, one for the object size, the other
	 * for the reference counter.
	 */
	public void allocate(Var v, int size) throws InsufficientMemory {
		super.allocate(v, size + 2);
		v.addr += 2;
		data[v.addr + SIZE] = size;
		data[v.addr + COUNTER] = 1;
	}

	public void assign(Var v1, Var v2) {
		if (!v1.isNull())
			decreaseCounter(v1.addr);
		super.assign(v1, v2);
		increaseCounter(v1.addr);
	}

	public void readField(Var v1, Var v2, int fieldOffset) {
		// TODO decrease counter
		if(!v1.isNull())
			decreaseCounter(v1.addr);

		super.readField(v1, v2, fieldOffset);

		// TODO increase counter
		increaseCounter(v1.addr);
	}

	public void writeField(Var v1, int fieldOffset, Var v2) {
		// TODO decrease counter

		if(data[v1.addr+fieldOffset]!=-1)
			decreaseCounter(data[v1.addr+fieldOffset]);

		super.writeField(v1, fieldOffset, v2);

		// TODO increase counter
		increaseCounter(data[v1.addr+fieldOffset]);
	}

	private void increaseCounter(int addr) {
		// TODO
		if(addr<0) return;
		data[addr+COUNTER]++;
	}

	private void decreaseCounter(int addr) {
		// TODO
		data[addr+COUNTER]--;
		if(data[addr+COUNTER]==0){
			for(int i=0; i<data[addr+SIZE]; i++){
				if(data[addr+i]!=-1)
					decreaseCounter(data[addr+i]);
			}
			freelist.release(addr-2,data[addr+SIZE]+2);
		}
	}
}
