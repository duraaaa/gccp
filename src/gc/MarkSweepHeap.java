package gc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MarkSweepHeap extends Heap {
	private static final int SIZE = -1;
	private static final int MARKER = -2;

	private int markTag = 0;
	private Set<Integer> allocatedObjectAddresses = new HashSet<Integer>();

	public MarkSweepHeap(int size) {
		super(size);
	}

	public void allocate(Var v, int size) throws InsufficientMemory {
		try {
			allocateObject(v, size);
		} catch (InsufficientMemory e) {
			markAndSweep();
			allocateObject(v, size);
		}
		// TODO
	}

	/**
	 * Allocate memory with 2 extra slots, one for the object size, the other
	 * for the marker.
	 */
	private void allocateObject(Var v, int size) throws InsufficientMemory {
		super.allocate(v, size + 2);
		// TODO
		v.addr += 2;
		data[v.addr+SIZE] = size;
		data[v.addr+MARKER] = 0;
		allocatedObjectAddresses.add(v.addr);
	}

	private void markAndSweep() {
		// TODO
		Set<Integer> temp = new HashSet<Integer>();
		for(Var var : reachable) {
			mark(var.addr);
		}
		for(Integer i : allocatedObjectAddresses) {
			if (sweep(i) == true) {
				temp.add(i);
			}
		}
		for(Integer i : temp){
			allocatedObjectAddresses.remove(i);
		}
	}

	private void mark(int addr) {
		// TODO
		if(addr<0) return;
		if(data[addr+MARKER]==1) return;
		data[addr+MARKER] = 1;
		for(int i = addr; i<addr+data[addr+SIZE]; ++i){
			if(data[i]>0) mark(data[i]);
		}

	}

	private boolean sweep(int addr) {
		// TODO
		if (data[addr+MARKER]==1) data[addr+MARKER] = 0;
		else {
			freelist.release(addr-2, data[addr+SIZE]+2);
			return true;
		}
		return false;
	}
}
