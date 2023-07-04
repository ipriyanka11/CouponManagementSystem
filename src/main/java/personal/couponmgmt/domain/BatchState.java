package personal.couponmgmt.domain;

public enum BatchState {
    CREATED {
        @Override
        public boolean allowedFrom(BatchState fromState) {
            if(fromState==null)
                return true;
            else return false;
        }
    },
    APPROVED {
        @Override
        public boolean allowedFrom(BatchState fromState) {
            if(fromState==BatchState.CREATED)
                return true;
            else return false;
        }
    },
    ACTIVE {
        @Override
        public boolean allowedFrom(BatchState fromState) {
            if(fromState==BatchState.APPROVED)
                return true;
            else return false;
        }
    },
    EXPIRED {
        @Override
        public boolean allowedFrom(BatchState fromState) {
            return true;
        }
    },
    SUSPENDED {
        @Override
        public boolean allowedFrom(BatchState fromState) {
            return true;
        }
    },
    TERMINATED {
        @Override
        public boolean allowedFrom(BatchState fromState) {
            return true;
        }
    };
    public abstract boolean allowedFrom(BatchState state);

}
