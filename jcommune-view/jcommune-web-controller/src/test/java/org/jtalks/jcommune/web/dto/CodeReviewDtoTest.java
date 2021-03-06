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

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.CodeReview;
import org.jtalks.jcommune.model.entity.CodeReviewComment;
import org.jtalks.jcommune.model.entity.JCUser;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * 
 * @author Vyacheslav Mishcheryakov
 *
 */
public class CodeReviewDtoTest {
    
    private static final long REVIEW_ID = 1L;
    
    @Test
    public void testDefaultConstructor() {
        CodeReviewDto dto = new CodeReviewDto();
        
        assertEquals(dto.getId(), 0);
        assertNotNull(dto.getComments());
        assertEquals(dto.getComments().size(), 0);
    }
    
    @Test
    public void testPrototypeConstructor() {
        CodeReview review = getCodeReview();
        
        CodeReviewDto dto = new CodeReviewDto(review);
        
        assertEquals(dto.getId(), REVIEW_ID);
        assertNotNull(dto.getComments());
        assertEquals(dto.getComments().size(), review.getComments().size());
    }
    
    private CodeReview getCodeReview() {
        CodeReview review = new CodeReview();
        review.setId(REVIEW_ID);
        
        CodeReviewComment comment1 = new CodeReviewComment();
        comment1.setId(1L);
        comment1.setAuthor(new JCUser("username1", "mail1", "password1" ));
        comment1.setBody("Comment1 body");
        comment1.setLineNumber(1);
        comment1.setCreationDate(new DateTime(1));
        review.addComment(comment1);
        
        CodeReviewComment comment2 = new CodeReviewComment();
        comment2.setId(2L);
        comment2.setAuthor(new JCUser("username2", "mail2", "password2" ));
        comment2.setBody("Comment2 body");
        comment2.setLineNumber(2);
        comment2.setCreationDate(new DateTime(2));
        review.addComment(comment1);

        return review;
    }
    
}
