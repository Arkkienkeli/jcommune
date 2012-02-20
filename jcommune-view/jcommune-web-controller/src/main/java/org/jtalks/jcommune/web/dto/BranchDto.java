/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.web.dto;

import org.jtalks.jcommune.model.entity.Branch;

/**
 * Dto for transferring branches to client side.
 *
 * @author Eugeny Batov
 */
public class BranchDto {

    private long id;
    private String name;

    /**
     * @return branch id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets branch id.
     *
     * @param id id of branch
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return branch name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets branch name.
     *
     * @param name name of branch
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Creates dto.
     *
     * @param branch branch for conversion
     * @return dto for branch
     */
    public static BranchDto getDtoFor(Branch branch) {
        BranchDto branchDto = new BranchDto();
        branchDto.setId(branch.getId());
        branchDto.setName(branch.getName());
        return branchDto;
    }
}