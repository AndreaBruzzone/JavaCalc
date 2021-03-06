package Pervasive.Sample;

import java.util.Enumeration;
import java.util.Vector;

public class MoneyBag implements IMoney {
	private Vector fMonies = new Vector(5);

	public MoneyBag() {
	}

	public MoneyBag(Money bag[]) {
		for (int i = 0; i < bag.length; i++) {
			if (!bag[i].isZero())
				appendMoney(bag[i]);
		}
	}

	public MoneyBag(Money m1, Money m2) {
		appendMoney(m1);
		appendMoney(m2);
	}

	public MoneyBag(Money m, MoneyBag bag) {
		appendMoney(m);
		appendBag(bag);
	}

	public MoneyBag(MoneyBag m1, MoneyBag m2) {
		appendBag(m1);
		appendBag(m2);
	}

	static IMoney create(IMoney m1, IMoney m2) {
		MoneyBag result = new MoneyBag();
		m1.appendTo(result);
		m2.appendTo(result);
		return result.simplify();
	}

	public IMoney add(IMoney m) {
		return m.addMoneyBag(this);
	}

	public IMoney addMoney(Money m) {
		return MoneyBag.create(m, this);
	}

	public IMoney addMoneyBag(MoneyBag s) {
		return MoneyBag.create(s, this);
	}

	void appendBag(MoneyBag aBag) {
		for (Enumeration e = aBag.fMonies.elements(); e.hasMoreElements();)
			appendMoney((Money) e.nextElement());
	}

	void appendMoney(Money aMoney) {
		if (aMoney.isZero())
			return;
		IMoney old = findMoney(aMoney.currency());
		if (old == null) {
			fMonies.addElement(aMoney);
			return;
		}
		fMonies.removeElement(old);
		IMoney sum = old.add(aMoney);
		if (sum.isZero())
			return;
		fMonies.addElement(sum);
	}

	public boolean equals(Object anObject) {
		if (isZero())
			if (anObject instanceof IMoney)
				return ((IMoney) anObject).isZero();

		if (anObject instanceof MoneyBag) {
			MoneyBag aMoneyBag = (MoneyBag) anObject;
			if (aMoneyBag.fMonies.size() != fMonies.size())
				return false;

			for (Enumeration e = fMonies.elements(); e.hasMoreElements();) {
				Money m = (Money) e.nextElement();
				if (!aMoneyBag.contains(m))
					return false;
			}
			return true;
		}
		return false;
	}

	private Money findMoney(String currency) {
		for (Enumeration e = fMonies.elements(); e.hasMoreElements();) {
			Money m = (Money) e.nextElement();
			if (m.currency().equals(currency))
				return m;
		}
		return null;
	}

	private boolean contains(Money m) {
		Money found = findMoney(m.currency());
		if (found == null)
			return false;
		return found.amount() == m.amount();
	}

	public int hashCode() {
		int hash = 0;
		for (Enumeration e = fMonies.elements(); e.hasMoreElements();) {
			Object m = e.nextElement();
			hash ^= m.hashCode();
		}
		return hash;
	}

	public boolean isZero() {
		return fMonies.size() == 0;
	}

	public IMoney multiply(int factor) {
		MoneyBag result = new MoneyBag();
		if (factor != 0) {
			for (Enumeration e = fMonies.elements(); e.hasMoreElements();) {
				Money m = (Money) e.nextElement();
				result.appendMoney((Money) m.multiply(factor));
			}
		}
		return result;
	}

	public IMoney negate() {
		MoneyBag result = new MoneyBag();
		for (Enumeration e = fMonies.elements(); e.hasMoreElements();) {
			Money m = (Money) e.nextElement();
			result.appendMoney((Money) m.negate());
		}
		return result;
	}

	private IMoney simplify() {
		if (fMonies.size() == 1)
			return (IMoney) fMonies.elements().nextElement();
		return this;
	}

	public IMoney subtract(IMoney m) {
		return add(m.negate());
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		for (Enumeration e = fMonies.elements(); e.hasMoreElements();)
			buffer.append(e.nextElement());
		buffer.append("}");
		return buffer.toString();
	}

	public void appendTo(MoneyBag m) {
		m.appendBag(this);
	}
}