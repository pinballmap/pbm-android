package com.pbm;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright (c) 2015, Brian Dols <brian.dols@gmail.com>
 * <p/>
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
public class Condition implements Serializable {
	private static final long serialVersionUID = 2470212492505135031L;
	private final Date date;

	private final String description;
	private int lmxId;
	private final int id;

	public Condition(int id, Date date, String description, int lmxId) {
		this.id = id;
		this.date = date;
		this.description = description;
		this.lmxId = lmxId;
	}

	public String getDescription() {
		return description;
	}

	public Date getDate() {
		return date;
	}

	public int getId() {
		return id;
	}

	public int getLmxId() {
		return lmxId;
	}
}
