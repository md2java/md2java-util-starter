package io.github.md2java.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NodeInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean lockActive;
	private LocalDateTime lockAtSince;
	private long lockAtMost;
	private String nodeId;

}
