package com.bedessee.sales.salesman;

public class Salesman {

    private String mName;
    private String mEmail;
    private String mEmailPrefix;
    private boolean mIsAdmin;

    public Salesman (String name, String email) {
        mName = name;
        mEmail = email;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public boolean isAdmin() {
        return mIsAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        mIsAdmin = isAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Salesman)) return false;

        Salesman salesman = (Salesman) o;

        return !(mEmail != null ? !mEmail.equals(salesman.mEmail) : salesman.mEmail != null);

    }

    @Override
    public int hashCode() {
        return mEmail != null ? mEmail.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Salesman{" +
                "mEmail='" + mEmail + '\'' +
                ", mEmailPrefix='" + mEmailPrefix + '\'' +
                ", mName='" + mName + '\'' +
                ", mIsAdmin=" + mIsAdmin +
                '}';
    }

    public String getEmailPrefix() {
        return mEmailPrefix;
    }

    public void setEmailPrefix(String emailPrefix) {
        mEmailPrefix = emailPrefix;
    }
}
