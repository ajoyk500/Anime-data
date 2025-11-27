package com.github.git24j.core;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Rebase extends CAutoReleasable {
    static native void jniOptionsSetPayload(long optionsPtr, long payload);
    static native int jniAbort(long rebase);
    static native int jniCommit(
            Oid id,
            long rebase,
            long author,
            long committer,
            String messageEncoding,
            String message);
    static native int jniFinish(long rebase, long signature);
    static native void jniFree(long rebase);
    static native int jniInit(
            AtomicLong out, long repoPtr, long branch, long upstream, long onto, long opts);
    static native int jniInitOptions(long opts, int version);
    static native int jniInmemoryIndex(AtomicLong index, long rebase);
    static native int jniNext(AtomicLong operation, long rebase);
    static native byte[] jniOntoId(long rebase);
    static native String jniOntoName(long rebase);
    static native int jniOpen(AtomicLong out, long repoPtr, long opts);
    static native long jniOperationByindex(long rebase, int idx);
    static native int jniOperationCurrent(long rebase);
    static native int jniOperationEntrycount(long rebase);
    static native String jniOperationGetExec(long operationPtr);
    static native byte[] jniOperationGetId(long operationPtr);
    static native int jniOperationGetType(long operationPtr);
    static native void jniOptionsFree(long optsPtr);
    static native long jniOptionsGetCheckoutOptions(long optionsPtr);
    static native int jniOptionsGetInmemory(long optionsPtr);
    static native long jniOptionsGetMergeOptions(long optionsPtr);
    static native int jniOptionsGetQuiet(long optionsPtr);
    static native String jniOptionsGetRewriteNotesRef(long optionsPtr);
    static native int jniOptionsGetVersion(long optionsPtr);
    static native int jniOptionsInit(long opts, int version);
    static native int jniOptionsNew(AtomicLong outOpts, int version);
    static native void jniOptionsSetInmemory(long optionsPtr, int inmemory);
    static native void jniOptionsSetQuiet(long optionsPtr, int quiet);
    static native void jniOptionsSetRewriteNotesRef(long optionsPtr, String rewriteNotesRef);
    static native void jniOptionsSetSigningCb(long optionsPtr, Internals.SSSCallback signingCb);
    static native void jniOptionsSetVersion(long optionsPtr, int version);
    static native byte[] jniOrigHeadId(long rebase);
    static native String jniOrigHeadName(long rebase);
    protected Rebase(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @Nonnull
    public static Rebase init(
            @Nonnull Repository repo,
            @Nullable AnnotatedCommit branch,
            @Nullable AnnotatedCommit upstream,
            @Nullable AnnotatedCommit onto,
            @Nullable Options opts) {
        Rebase rebase = new Rebase(false, 0);
        Error.throwIfNeeded(
                jniInit(
                        rebase._rawPtr,
                        repo.getRawPointer(),
                        branch == null ? 0 : branch.getRawPointer(),
                        upstream == null ? 0 : upstream.getRawPointer(),
                        onto == null ? 0 : onto.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer()));
        return rebase;
    }
    @Nonnull
    public static Rebase open(@Nonnull Repository repo, @Nullable Options opts) {
        Rebase rebase = new Rebase(false, 0);
        Error.throwIfNeeded(
                jniOpen(
                        rebase._rawPtr,
                        repo.getRawPointer(),
                        opts == null ? 0 : opts.getRawPointer()));
        return rebase;
    }
    @CheckForNull
    public Oid ontoId() {
        byte[] raw = jniOntoId(getRawPointer());
        if (raw == null) {
            return null;
        }
        return Oid.of(raw);
    }
    @CheckForNull
    public String ontoName() {
        return jniOntoName(getRawPointer());
    }
    @CheckForNull
    public Oid origHeadId() {
        byte[] raw = jniOrigHeadId(getRawPointer());
        return raw == null ? null : Oid.of(raw);
    }
    @CheckForNull
    public String origHeadName() {
        return jniOrigHeadName(getRawPointer());
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    public int operationEntrycount() {
        return jniOperationEntrycount(getRawPointer());
    }
    public int operationCurrent() {
        return jniOperationCurrent(getRawPointer());
    }
    @Nullable
    public Operation operationByIndex(int idx) {
        long ptr = jniOperationByindex(getRawPointer(), idx);
        if (ptr == 0) {
            return null;
        }
        return new Operation(ptr);
    }
    @Nonnull
    public Operation next() {
        Operation out = new Operation(0);
        Error.throwIfNeeded(jniNext(out._rawPtr, getRawPointer()));
        return out;
    }
    @Nonnull
    public Index inmemoryIndex() {
        Index outIdx = new Index(false, 0);
        Error.throwIfNeeded(jniInmemoryIndex(outIdx._rawPtr, getRawPointer()));
        return outIdx;
    }
    public Oid commit(
            @Nullable Signature author,
            @Nonnull Signature committer,
            @Nullable Charset messageEncoding,
            @Nullable String message) {
        Oid oid = new Oid();
        Error.throwIfNeeded(
                jniCommit(
                        oid,
                        getRawPointer(),
                        author == null ? 0 : author.getRawPointer(),
                        committer.getRawPointer(),
                        messageEncoding == null ? null : messageEncoding.name(),
                        message));
        return oid;
    }
    public void abort() {
        Error.throwIfNeeded(jniAbort(getRawPointer()));
    }
    public void finish(@Nullable Signature signature) {
        Error.throwIfNeeded(
                jniFinish(getRawPointer(), signature == null ? 0 : signature.getRawPointer()));
    }
    public enum OperationT implements IBitEnum {
        PICK(0),
        REWORD(1),
        EDIT(2),
        SQUASH(3),
        FIXUP(4),
        EXEC(5);
        private final int _bit;
        OperationT(int bit) {
            this._bit = bit;
        }
        @Override
        public int getBit() {
            return _bit;
        }
    }
    public static class Options extends CAutoReleasable {
        public static final int VERSION = 1;
        protected Options(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }
        @Nonnull
        public static Options create(int version) {
            Options opts = new Options(false, 0);
            Error.throwIfNeeded(jniOptionsNew(opts._rawPtr, version));
            return opts;
        }
        public static Options createDefault() {
            return create(VERSION);
        }
        @Override
        protected void freeOnce(long cPtr) {
            jniOptionsFree(cPtr);
        }
        public int getVersion() {
            return jniOptionsGetVersion(getRawPointer());
        }
        public void setVersion(int version) {
            jniOptionsSetVersion(getRawPointer(), version);
        }
        public int getQuiet() {
            return jniOptionsGetQuiet(getRawPointer());
        }
        public void setQuiet(int quiet) {
            jniOptionsSetQuiet(getRawPointer(), quiet);
        }
        public int getInmemory() {
            return jniOptionsGetInmemory(getRawPointer());
        }
        public void setInmemory(int inmemory) {
            jniOptionsSetInmemory(getRawPointer(), inmemory);
        }
        public String getRewriteNotesRef() {
            return jniOptionsGetRewriteNotesRef(getRawPointer());
        }
        public void setRewriteNotesRef(String rewriteNotesRef) {
            jniOptionsSetRewriteNotesRef(getRawPointer(), rewriteNotesRef);
        }
        @Nonnull
        public Merge.Options getMergeOptions() {
            long ptr = jniOptionsGetMergeOptions(getRawPointer());
            return new Merge.Options(true, ptr);
        }
        public Checkout.Options getCheckoutOptions() {
            return new Checkout.Options(true, jniOptionsGetCheckoutOptions(getRawPointer()));
        }
        public void setSigningCb(Commit.SigningCb signingCb) {
            jniOptionsSetSigningCb(getRawPointer(), signingCb::accept);
        }
        public void setPayload(long payload) {
            jniOptionsSetPayload(getRawPointer(), payload);
        }
    }
    public static class Operation extends CAutoReleasable {
        protected Operation(long rawPtr) {
            super(true, rawPtr);
        }
        @Override
        protected void freeOnce(long cPtr) {
        }
        @CheckForNull
        public OperationT getType() {
            int r = jniOperationGetType(getRawPointer());
            return IBitEnum.valueOf(r, OperationT.class);
        }
        @CheckForNull
        public Oid getId() {
            byte[] raw = jniOperationGetId(this.getRawPointer());
            return raw == null ? null : Oid.of(raw);
        }
        @CheckForNull
        public String getExec() {
            return jniOperationGetExec(getRawPointer());
        }
    }
}
