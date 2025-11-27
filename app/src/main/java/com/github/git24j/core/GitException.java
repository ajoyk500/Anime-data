package com.github.git24j.core;


public class GitException extends RuntimeException {
    private final String message;
    private ErrorCode code;
    private ErrorClass klass;
    public GitException(int klass, String message) {
        this(ErrorClass.of(klass), message);
    }
    public GitException(ErrorClass klass, String message) {
        super(message);
        this.klass = klass;
        this.message = message;
    }
    public ErrorClass getErrorClass() {
        return klass;
    }
    public ErrorCode getCode() {
        return code;
    }
    public void setCode(int rawCode) {
        code = ErrorCode.of(rawCode);
    }
    public enum ErrorCode {
        OK(0),
        ERROR(-1),
        ENOTFOUND(-3),
        EEXISTS(-4),
        EAMBIGUOUS(-5),
        EBUFS(-6),
        EUSER(-7),
        EBAREREPO(-8),
        EUNBORNBRANCH(-9),
        EUNMERGED(-10),
        ENONFASTFORWARD(-11),
        EINVALIDSPEC(-12),
        ECONFLICT(-13),
        ELOCKED(-14),
        EMODIFIED(-15),
        EAUTH(-16),
        ECERTIFICATE(-17),
        EAPPLIED(-18),
        EPEEL(-19),
        EEOF(-20),
        EINVALID(-21),
        EUNCOMMITTED(-22),
        EDIRECTORY(-23),
        EMERGECONFLICT(-24),
        PASSTHROUGH(-30),
        ITEROVER(-31),
        RETRY(-32),
        EMISMATCH(-33),
        EINDEXDIRTY(-34),
        EAPPLYFAIL(-35),
        EOWNER(-36),
        TIMEOUT(-37),
        EUNCHANGED(-38),
        ENOTSUPPORTED(-39),
        EREADONLY(-40),
        UNKNOWN(-9999);
        private final int code;
        ErrorCode(int code) {
            this.code = code;
        }
        public static ErrorCode of(int gitErrorCode) {
            for (ErrorCode c : ErrorCode.values()) {
                if (c.code == gitErrorCode) {
                    return c;
                }
            }
            return UNKNOWN;
        }
        public int getCode() {
            return code;
        }
    }
    public enum ErrorClass {
        NONE,
        NOMEMORY,
        OS,
        INVALID,
        REFERENCE,
        ZLIB,
        REPOSITORY,
        CONFIG,
        REGEX,
        ODB,
        INDEX,
        OBJECT,
        NET,
        TAG,
        TREE,
        INDEXER,
        SSL,
        SUBMODULE,
        THREAD,
        STASH,
        CHECKOUT,
        FETCHHEAD,
        MERGE,
        SSH,
        FILTER,
        REVERT,
        CALLBACK,
        CHERRYPICK,
        DESCRIBE,
        REBASE,
        FILESYSTEM,
        PATCH,
        WORKTREE,
        SHA,
        HTTP,
        INTERNAL,
        GRAFTS;
        static ErrorClass of(int klass) {
            ErrorClass[] values = ErrorClass.values();
            if (klass >= 0 && klass < values.length) {
                return values[klass];
            }
            return null;
        }
    }
}
