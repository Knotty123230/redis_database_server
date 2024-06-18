# Redis with Replication

## Overview

<p>This project demonstrates a Redis setup with replication capabilities, managed by a Java application. The primary Redis server can replicate its data to one or more replicas, ensuring data redundancy and high availability.</p>

## Features

<ul>
  <li><strong>Replication</strong>: Easily set up a Redis replica to mirror the data from the primary server.</li>
  <li><strong>Configurable Ports</strong>: Customize the port for each Redis instance through command-line arguments.</li>
</ul>

## Getting Started

### Prerequisites

<ul>
  <li>Java 21</li>
</ul>

### Installation

<ol>
  <li><strong>Clone the repository</strong>:
    <pre><code>git clone https://github.com/Knotty123230/redis_database_server.git
cd redis_database_server</code></pre>
  </li>
  <li><strong>Build the project</strong>:
    <pre><code>mvn clean package</code></pre>
  </li>
</ol>

### Usage

#### Running the Primary Redis Server

<p>To start the primary Redis server on a specified port:</p>
<pre><code>java -jar target/redis-replication-java.jar --port &lt;port&gt;</code></pre>
<p>Replace <code>&lt;port&gt;</code> with your desired port number.</p>

#### Running a Redis Replica

<p>To start a Redis replica that connects to the primary server:</p>
<pre><code>java -jar target/redis-replication-java.jar --port &lt;replica-port&gt; --replicaof &lt;primary-host&gt; &lt;primary-port&gt;</code></pre>
<p>Replace <code>&lt;replica-port&gt;</code>, <code>&lt;primary-host&gt;</code>, and <code>&lt;primary-port&gt;</code> with the appropriate values.</p>

#### Example

<p>Start the primary server on port 6379:</p>
<pre><code>java -jar build/libs/redis-replication-java.jar --port 6379</code></pre>

<p>Start a replica server on port 6380, replicating from the primary server:</p>
<pre><code>java -jar build/libs/redis-replication-java.jar --port 6380 --replicaof 127.0.0.1 6379</code></pre>

### Persistence with RDB Files

<p>Redis supports RDB (Redis Database Backup) files for data persistence. By default, the RDB file is created in the working directory of the Redis server. You can configure the location and frequency of RDB snapshots in the Redis configuration file (<code>redis.conf</code>).</p>

<p>To manually trigger an RDB save, run:</p>
<pre><code>redis-cli save</code></pre>

<p>To configure automated snapshots, edit the <code>redis.conf</code> file:</p>
<pre><code>save 900 1
save 300 10
save 60 10000</code></pre>

## Configuration

### Command-Line Options

<ul>
  <li><code>--port &lt;port&gt;</code>: Specify the port on which the Redis server will listen.</li>
  <li><code>--replicaof &lt;primary-host&gt; &lt;primary-port&gt;</code>: Set up the server as a replica of the primary server.</li>
</ul>

### Example Configuration

<p>To start a Redis server on port 6379 and set it as a replica of a primary server on <code>primary.example.com</code> port 6380:</p>
<pre><code>java -jar target/redis-replication-java.jar --port 6379 --replicaof primary.example.com 6380</code></pre>

