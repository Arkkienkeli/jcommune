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
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;

/**
 * @author Evgeniy Naumenko
 */
public interface SubscriptionService {

    /**
     * Subscribes current user to the topic given.
     * That means, that user will receive email notification for every
     * topic update except for topic removal.
     *
     * @param topic topic to subscribe current user to
     */
    void subscribeToTopic(Topic topic);

    /**
     *
     * @param branch
     */
    void subscribeToBranch(Branch branch);

    /**
     *
     * @param topic topic to unsubscribe current user from
     */
    void unsubscribeFromTopic(Topic topic);

    /**
     *
     * @param branch
     */
    void unsubscribeFromBranch(Branch branch);
}
