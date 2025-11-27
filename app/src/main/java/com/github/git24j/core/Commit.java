package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Commit extends GitObject {
    static native int jniAmend(
            Oid outOid,
            long commitToAmend,
            String updateRef,
            long author,
            long committer,
            String messageEncoding,
            String message,
            long treePtr);
    static native long jniAuthor(long commitPtr);
    static native int jniAuthorWithMailmap(AtomicLong out, long commit, long mailmap);
    static native String jniBody(long commitPtr);
    static native long jniCommitter(long commitPtr);
    static native int jniCommitterWithMailmap(AtomicLong out, long commit, long mailmap);
    static native int jniCreate(
            Oid outOid,
            long repoPtr,
            String updateRef,
            long author,
            long commiter,
            String msgEncoding,
            String message,
            long treePtr,
            long[] parents);
    static native int jniCreateBuffer(
            Buf outBuf,
            long repoPtr,
            long author,
            long committer,
            String messageEncoding,
            String message,
            long treePtr,
            int parentCnt,
            long[] parents);
    static native int jniCreateWithSignature(
            Oid oid, long repoPtr, String commitContent, String signature, String signatureField);
    static native int jniHeaderField(Buf outBuf, long commitPtr, String field);
    static native String jniMessage(long commitPtr);
    static native String jniMessageEncoding(long commitPtr);
    static native String jniMessageRaw(long commitPtr);
    static native int jniNthGenAncestor(AtomicLong outPtr, long commitPtr, int n);
    static native int jniParent(AtomicLong outPtr, long commitPtr, int n);
    static native int jniParentCount(long commitPtr);
    static native byte[] jniParentId(long commitPtr, int n);
    static native String jniRawHeader(long commitPtr);
    static native String jniSummary(long commitPtr);
    static native long jniTime(long commitPtr);
    static native int jniTimeOffset(long commitPtr);
    static native int jniTree(AtomicLong outTreePtr, long commitPtr);
    static native byte[] jniTreeId(long commitPtr);
    Commit(boolean weak, long rawPointer) {
        super(weak, rawPointer);
    }
    public static Commit lookup(@Nonnull Repository repo, @Nonnull Oid oid) {
        return (Commit) GitObject.lookup(repo, oid, Type.COMMIT);
    }
    public static Commit lookupPrefix(@Nonnull Repository repo, @Nonnull String shortOid) {
        return (Commit) GitObject.lookupPrefix(repo, shortOid, Type.COMMIT);
    }
    public static Oid create(
            @Nonnull Repository repo,
            @Nullable String updateRef,
            @Nonnull Signature author,
            @Nonnull Signature committer,
            @Nullable String messageEncoding,
            @Nonnull String message,
            @Nonnull Tree tree,
            @Nonnull List<Commit> parents) {
        Oid outOid = new Oid();
        long[] parentsArray =
                parents.stream().map(Commit::getRawPointer).mapToLong(Long::longValue).toArray();
        int e =
                jniCreate(
                        outOid,
                        repo.getRawPointer(),
                        updateRef,
                        author.getRawPointer(),
                        committer.getRawPointer(),
                        messageEncoding,
                        message,
                        tree.getRawPointer(),
                        parentsArray);
        Error.throwIfNeeded(e);
        return outOid;
    }
    public static Oid amend(
            Commit commitToAmend,
            @Nullable String updateRef,
            @Nullable Signature author,
            @Nullable Signature committer,
            @Nullable String messageEncoding,
            @Nullable String message,
            @Nullable Tree tree) {
        Oid outOid = new Oid();
        int e =
                jniAmend(
                        outOid,
                        commitToAmend.getRawPointer(),
                        updateRef,
                        author == null ? 0 : author.getRawPointer(),
                        committer == null ? 0 : committer.getRawPointer(),
                        messageEncoding,
                        message,
                        tree == null ? 0 : tree.getRawPointer());
        Error.throwIfNeeded(e);
        return outOid;
    }
    public static Buf createBuffer(
            @Nonnull Repository repo,
            @Nonnull Signature author,
            @Nonnull Signature committer,
            @Nullable String messageEncoding,
            @Nonnull String message,
            @Nonnull Tree tree,
            @Nonnull List<Commit> parents) {
        Buf outBuf = new Buf();
        long[] parentsArray =
                parents.stream().map(Commit::getRawPointer).mapToLong(Long::longValue).toArray();
        int e =
                jniCreateBuffer(
                        outBuf,
                        repo.getRawPointer(),
                        author.getRawPointer(),
                        committer.getRawPointer(),
                        messageEncoding,
                        message,
                        tree.getRawPointer(),
                        parents.size(),
                        parentsArray);
        Error.throwIfNeeded(e);
        return outBuf;
    }
    public static Oid createWithSignature(
            Repository repo, String commitContent, String signature, String signatureField) {
        Oid outOid = new Oid();
        Error.throwIfNeeded(
                jniCreateWithSignature(
                        outOid, repo.getRawPointer(), commitContent, signature, signatureField));
        return outOid;
    }
    public String messageEncoding() {
        return jniMessageEncoding(getRawPointer());
    }
    public String message() {
        return jniMessage(getRawPointer());
    }
    public String messageRaw() {
        return jniMessageRaw(getRawPointer());
    }
    public String summary() {
        return jniSummary(getRawPointer());
    }
    public String body() {
        return jniBody(getRawPointer());
    }
    public Instant time() {
        return Instant.ofEpochSecond(jniTime(getRawPointer()));
    }
    public int timeOffset() {
        return jniTimeOffset(getRawPointer());
    }
    @Nonnull
    public Signature committer() {
        long ptr = jniCommitter(getRawPointer());
        return new Signature(true, ptr);
    }
    @Nonnull
    public Signature author() {
        long ptr = jniAuthor(getRawPointer());
        return new Signature(true, ptr);
    }
    @Nullable
    public Signature committerWithMailmap(@Nullable Mailmap mailmap) {
        Signature outSig = new Signature(false, 0);
        Error.throwIfNeeded(
                jniCommitterWithMailmap(
                        outSig._rawPtr,
                        getRawPointer(),
                        mailmap == null ? 0 : mailmap.getRawPointer()));
        return outSig.isNull() ? null : outSig;
    }
    @Nullable
    public Signature authorWithMailmap(@Nullable Mailmap mailmap) {
        Signature outSig = new Signature(false, 0);
        Error.throwIfNeeded(
                jniAuthorWithMailmap(
                        outSig._rawPtr,
                        getRawPointer(),
                        mailmap == null ? 0 : mailmap.getRawPointer()));
        return outSig.isNull() ? null : outSig;
    }
    public String rawHeader() {
        return jniRawHeader(getRawPointer());
    }
    public Tree tree() {
        AtomicLong outTreePtr = new AtomicLong();
        Error.throwIfNeeded(jniTree(outTreePtr, getRawPointer()));
        return new Tree(false, outTreePtr.get());
    }
    @CheckForNull
    public Oid treeId() {
        return Oid.ofNullable(jniTreeId(getRawPointer()));
    }
    public int parentCount() {
        return jniParentCount(getRawPointer());
    }
    public Commit parent(int n) {
        AtomicLong outPtr = new AtomicLong();
        Error.throwIfNeeded(jniParent(outPtr, getRawPointer(), n));
        return new Commit(false, outPtr.get());
    }
    @CheckForNull
    public Oid parentId(int n) {
        return Oid.ofNullable(jniParentId(getRawPointer(), n));
    }
    public Commit nthGenAncestor(int n) {
        AtomicLong outPtr = new AtomicLong();
        int e = jniNthGenAncestor(outPtr, getRawPointer(), n);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Commit(false, outPtr.get());
    }
    @Nullable
    public String headerField(String field) {
        Buf buf = new Buf();
        int e = jniHeaderField(buf, getRawPointer(), field);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return buf.getString().orElse(null);
    }
    @Override
    public Commit dup() {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniDup(out, getRawPointer()));
        return new Commit(false, out.get());
    }
    @FunctionalInterface
    public interface SigningCb {
        int accept(String signature, String signatureField, String commitContent);
    }
}
