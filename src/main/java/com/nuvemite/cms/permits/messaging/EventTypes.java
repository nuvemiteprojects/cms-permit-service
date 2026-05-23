package com.nuvemite.cms.permits.messaging;

public final class EventTypes {

    public static final String CONSUMER_GROUP = "cms-permits";

    public static final String LICENSE_GRANTED = "cms.license.granted.v1";
    public static final String LICENSE_REVOKED = "cms.license.revoked.v1";
    public static final String PERMIT_APPROVED = "cms.permit.approved.v1";

    private EventTypes() {}
}
