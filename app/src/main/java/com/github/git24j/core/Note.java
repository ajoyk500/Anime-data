package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Note extends CAutoReleasable {
    static native long jniAuthor(long note);
    static native int jniCommitCreate(
            Oid notes_commit_out,
            Oid notes_blob_out,
            long repoPtr,
            long parent,
            long author,
            long committer,
            Oid oid,
            String note,
            int allowNoteOverwrite);
    static native int jniCommitRead(AtomicLong out, long repoPtr, long notesCommit, Oid oid);
    static native int jniCommitRemove(
            Oid notes_commit_out,
            long repoPtr,
            long notesCommit,
            long author,
            long committer,
            Oid oid);
    static native long jniCommitter(long note);
    static native int jniCreate(
            Oid out,
            long repoPtr,
            String notesRef,
            long author,
            long committer,
            Oid oid,
            String note,
            int force);
    static native int jniDefaultRef(Buf out, long repoPtr);
    static native int jniForeach(
            long repoPtr, String notesRef, Internals.BArrBarrCallback bbCallback);
    static native void jniFree(long note);
    static native byte[] jniId(long note);
    static native String jniMessage(long note);
    static native int jniRead(AtomicLong out, long repoPtr, String notesRef, Oid oid);
    static native int jniRemove(
            long repoPtr, String notesRef, long author, long committer, Oid oid);
    protected Note(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }
    @CheckForNull
    public static Note read(@Nonnull Repository repo, @Nullable String notesRef, @Nonnull Oid oid) {
        Note out = new Note(false, 0);
        int e = jniRead(out._rawPtr, repo.getRawPointer(), notesRef, oid);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }
    @CheckForNull
    public static Note commitRead(
            @Nonnull Repository repo, @Nonnull Commit notesCommit, @Nonnull Oid oid) {
        Note note = new Note(false, 0);
        int e = jniCommitRead(note._rawPtr, repo.getRawPointer(), notesCommit.getRawPointer(), oid);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return note;
    }
    @CheckForNull
    public static Oid create(
            @Nonnull Repository repo,
            @Nullable String notesRef,
            @Nonnull Signature author,
            @Nonnull Signature committer,
            @Nonnull Oid oid,
            @Nonnull String note,
            boolean force) {
        Oid outOid = new Oid();
        Error.throwIfNeeded(
                jniCreate(
                        outOid,
                        repo.getRawPointer(),
                        notesRef,
                        author.getRawPointer(),
                        committer.getRawPointer(),
                        oid,
                        note,
                        force ? 1 : 0));
        return outOid.getId() == null ? null : outOid;
    }
    @Nonnull
    public static CommitCreateResult commitCreate(
            @Nonnull Repository repo,
            @Nullable Commit parent,
            @Nonnull Signature author,
            @Nonnull Signature committer,
            @Nonnull Oid oid,
            @Nonnull String note,
            boolean allowNoteOverwrite) {
        Oid notesCommitOut = new Oid();
        Oid notesBlobOut = new Oid();
        Error.throwIfNeeded(
                jniCommitCreate(
                        notesCommitOut,
                        notesBlobOut,
                        repo.getRawPointer(),
                        parent == null ? 0 : parent.getRawPointer(),
                        author.getRawPointer(),
                        committer.getRawPointer(),
                        oid,
                        note,
                        allowNoteOverwrite ? 1 : 0));
        return new CommitCreateResult(notesCommitOut, notesBlobOut);
    }
    public static void remove(
            @Nonnull Repository repo,
            @Nullable String notesRef,
            @Nonnull Signature author,
            @Nonnull Signature committer,
            @Nonnull Oid oid) {
        Error.throwIfNeeded(
                jniRemove(
                        repo.getRawPointer(),
                        notesRef,
                        author.getRawPointer(),
                        committer.getRawPointer(),
                        oid));
    }
    public static Oid commitRemove(
            @Nonnull Repository repo,
            @Nonnull Commit notesCommit,
            @Nonnull Signature author,
            @Nonnull Signature committer,
            @Nonnull Oid oid) {
        Oid out = new Oid();
        Error.throwIfNeeded(
                jniCommitRemove(
                        out,
                        repo.getRawPointer(),
                        notesCommit.getRawPointer(),
                        author.getRawPointer(),
                        committer.getRawPointer(),
                        oid));
        return out.getId() == null ? null : out;
    }
    @CheckForNull
    public static String defaultRef(Repository repo) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniDefaultRef(out, repo.getRawPointer()));
        return out.getString().orElse(null);
    }
    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }
    @CheckForNull
    public Signature author() {
        long ptr = jniAuthor(getRawPointer());
        if (ptr == 0) {
            return null;
        }
        return new Signature(true, ptr);
    }
    public Signature committer() {
        long ptr = jniCommitter(getRawPointer());
        return ptr == 0 ? null : new Signature(true, ptr);
    }
    @Nonnull
    public String message() {
        String msg = jniMessage(getRawPointer());
        return msg == null ? "" : msg;
    }
    @CheckForNull
    public Oid id() {
        byte[] raw = jniId(getRawPointer());
        return raw == null ? null : Oid.of(raw);
    }
    public void foreach(
            @Nonnull Repository repo, @Nullable String notesRef, @Nonnull ForeachCb cb) {
        Error.throwIfNeeded(
                jniForeach(
                        repo.getRawPointer(),
                        notesRef,
                        (id1, id2) -> cb.accept(Oid.of(id1), Oid.of(id2))));
    }
    @FunctionalInterface
    public interface ForeachCb {
        int accept(Oid blobId, Oid annotatedObjectId);
    }
    public static class CommitCreateResult {
        private final Oid _commit;
        private final Oid _blob;
        public CommitCreateResult(@Nullable Oid commit, @Nullable Oid blob) {
            _commit = commit;
            _blob = blob;
        }
        public Oid getCommit() {
            return _commit;
        }
        public Oid getBlob() {
            return _blob;
        }
    }
}
