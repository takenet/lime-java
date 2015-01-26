package org.limeprotocol;


import org.limeprotocol.util.Cast;
import org.limeprotocol.util.StringUtils;

public class Node extends Identity {


    private String instance;
    private boolean isComplete;


    public Node(String name, String domain, String instance){
        this(name, domain);
        setInstance(instance);
    }

    public Node(String name, String domain){
        super(name, domain);
    }

    /// <summary>
    /// The name of the instance used
    /// by the node to connect to the network
    /// </summary>
    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    @Override
    public String toString()
    {
        //'\0' == '' (Empty character)
        String nodeString = StringUtils.format("{0}/{1}", super.toString(), instance);
        nodeString = StringUtils.trimEnd(nodeString, "/");

        return nodeString;
    }

    @Override
    public boolean equals(Object obj)
    {
        Node node = Cast.as(Node.class, obj);

        if (node == null)
        {
            return false;
        }

        return ((this.getName() == null && node.getName() == null) || (this.getName() != null && this.getName().equalsIgnoreCase(node.getName()))) &&
                ((this.getDomain() == null && node.getDomain() == null) || (this.getDomain() != null && this.getDomain().equalsIgnoreCase(node.getDomain()))) &&
                ((this.getInstance() == null && node.getInstance() == null) || (this.getInstance() != null && this.getInstance().equalsIgnoreCase(node.getInstance())));
    }

    /// <summary>
    /// Returns a hash code for this instance.
    /// </summary>
    /// <returns>
    /// A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table.
    /// </returns>
    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    /// <summary>
    /// Parses the String to a valid Node.
    /// </summary>
    /// <param name="s">The s.</param>
    /// <returns></returns>
    /// <exception cref="System.ArgumentNullException">s</exception>
    /// <exception cref="System.FormatException">Invalid Peer format</exception>
    public static Node parse(String s)
    {
        if (StringUtils.isNullOrWhiteSpace(s)) {
            throw new IllegalArgumentException("s");
        }

        Identity identity = Identity.parse(s);

        String[] splittedDomain = identity.getDomain() != null ? identity.getDomain().split("/") : null;

        String name = identity.getName();
        String domain = splittedDomain != null ? splittedDomain[0] : null;
        String instance = splittedDomain != null && splittedDomain.length > 1 ? splittedDomain[1] : null;

        return new Node(name, domain, instance);
    }

    /// <summary>
    /// Tries to parse the String to a valid Node
    /// </summary>
    /// <param name="s">The s.</param>
    /// <param name="value">The value.</param>
    /// <returns></returns>
    /// WARNING: Remember that Java hasn't out-operator!
    ///TODO: Check if all call to this method execute parser again
    public static boolean TryParse(String s, Node value)
    {
        try
        {
            parse(s);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /// <summary>
    /// Creates an Identity instance
    /// based on the Node identity
    /// </summary>
    /// <returns></returns>
    public Identity toIdentity()
    {
        return new Identity(getName(), getDomain());
    }

    /// <summary>
    /// Indicates if the node is
    /// a complete representation, with
    /// name, domain and instance.
    /// </summary>
    public boolean isComplete(){

        return
                    !StringUtils.isNullOrEmpty(getName()) &&
                            !StringUtils.isNullOrEmpty(getDomain()) &&
                            !StringUtils.isNullOrEmpty(getInstance());

    }

    /// <summary>
    /// Creates a new object that
    /// is a copy of the current instance.
    /// </summary>
    /// <returns>
    /// A new object that is a copy of this instance.
    /// </returns>
    public Node copy(){
        return new Node(getName(), getDomain(), getInstance());
    }

}
