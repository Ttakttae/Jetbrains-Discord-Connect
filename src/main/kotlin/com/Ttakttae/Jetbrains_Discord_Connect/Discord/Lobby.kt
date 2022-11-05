package com.Ttakttae.Jetbrains_Discord_Connect.Discord

import de.jcm.discordgamesdk.*
import de.jcm.discordgamesdk.activity.Activity
import de.jcm.discordgamesdk.lobby.Lobby
import de.jcm.discordgamesdk.lobby.LobbyType
import de.jcm.discordgamesdk.user.DiscordUser
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.event.*
import java.io.File
import java.io.IOException
import java.util.concurrent.*
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.swing.*
import javax.swing.border.LineBorder


class Lobby : JFrame() {
    private var selfUser: DiscordUser? = null
    private val myLobbies: MutableList<Lobby> = java.util.ArrayList()
    private val joinedLobbies: MutableList<Lobby> = java.util.ArrayList()
    private val activity = Activity()
    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val eventAdapter: DiscordEventAdapter = object : DiscordEventAdapter() {
        override fun onLobbyUpdate(lobbyId: Long) {
            if (myLobbies.removeIf { l: Lobby -> l.id == lobbyId }) {
                myLobbies.add(core!!.lobbyManager().getLobby(lobbyId))
            }
            if (joinedLobbies.removeIf { l: Lobby -> l.id == lobbyId }) {
                joinedLobbies.add(core!!.lobbyManager().getLobby(lobbyId))
            }
        }

        override fun onLobbyDelete(lobbyId: Long, reason: Int) {
            myLobbies.removeIf { l: Lobby -> l.id == lobbyId }
            joinedLobbies.removeIf { l: Lobby -> l.id == lobbyId }
        }

        override fun onMemberConnect(lobbyId: Long, userId: Long) {
            // Add some code here
        }

        override fun onMemberUpdate(lobbyId: Long, userId: Long) {
            // Add some code here
        }

        override fun onMemberDisconnect(lobbyId: Long, userId: Long) {
            // Add some code here
        }

        override fun onCurrentUserUpdate() {
            selfUser = core!!.userManager().currentUser
            title = "Lobby Manager: " +
                    (selfUser ?: return).getUsername() + "#" + (selfUser ?: return).getDiscriminator() + " " + (selfUser ?: return).getUserId()
        }

        override fun onActivityJoin(secret: String) {
            core!!.lobbyManager().connectLobbyWithActivitySecret(
                secret
            ) { result: Result, lobby: Lobby ->
                JOptionPane.showMessageDialog(
                    null, result.name, "Result",
                    if (result == Result.OK) JOptionPane.INFORMATION_MESSAGE else JOptionPane.ERROR_MESSAGE
                )
                if (result == Result.OK) {
                    joinedLobbies.add(lobby)
                }
            }
        }
    }

    init {
        val contentPane = JPanel(BorderLayout())
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                val futures = java.util.ArrayList<CompletableFuture<Void?>>()
                for (lobby in myLobbies) {
                    val f = CompletableFuture<Void?>()
                    core!!.lobbyManager().deleteLobby(
                        lobby
                    ) { result: Result ->
                        if (result == Result.OK) f.complete(
                            null
                        ) else f.completeExceptionally(GameSDKException(result))
                    }
                    futures.add(f)
                }
                try {
                    CompletableFuture.allOf(*futures.toTypedArray<CompletableFuture<*>>()).get()
                } catch (interruptedException: InterruptedException) {
                    interruptedException.printStackTrace()
                } catch (interruptedException: ExecutionException) {
                    interruptedException.printStackTrace()
                }
                callbackFuture!!.cancel(true)
                updateFuture.cancel(true)
                executor!!.shutdown()
                core!!.close()
                createParams!!.close()
            }
        })
        val createLobby = JPanel(FlowLayout(FlowLayout.LEADING))
        createLobby.add(JLabel("Type: "))
        val type = JComboBox(LobbyType.values())
        createLobby.add(type)
        createLobby.add(JLabel("Capacity: "))
        val capacity = JSpinner(SpinnerNumberModel(10, 1, 1000, 1))
        createLobby.add(capacity)
        val locked = JCheckBox("Locked")
        createLobby.add(locked)
        val createButton = JButton("Create Lobby")
        createButton.addActionListener { _: ActionEvent? ->
            val txn = core!!.lobbyManager().lobbyCreateTransaction
            txn.setType(type.selectedItem as LobbyType)
            txn.setCapacity((capacity.value as Int))
            txn.setLocked(locked.isSelected)
            core!!.lobbyManager().createLobby(
                txn
            ) { result: Result, lobby: Lobby ->
                JOptionPane.showMessageDialog(
                    null, result.name, "Result",
                    if (result == Result.OK) JOptionPane.INFORMATION_MESSAGE else JOptionPane.ERROR_MESSAGE
                )
                if (result == Result.OK) {
                    myLobbies.add(lobby)
                }
            }
        }
        createLobby.add(createButton)
        val joinPanel = JPanel(FlowLayout(FlowLayout.LEADING))
        joinPanel.add(JLabel("ID: "))
        val connectLobbyId = JTextField(16)
        joinPanel.add(connectLobbyId)
        joinPanel.add(JLabel("Secret: "))
        val connectSecret = JTextField(16)
        joinPanel.add(connectSecret)
        val connectByIdButton = JButton("Connect")
        connectByIdButton.addActionListener { _: ActionEvent? ->
            val lobbyId = connectLobbyId.text.toLong()
            val secret = connectSecret.text
            core!!.lobbyManager().connectLobby(
                lobbyId, secret
            ) { result: Result, lobby: Lobby ->
                JOptionPane.showMessageDialog(
                    null, result.name, "Result",
                    if (result == Result.OK) JOptionPane.INFORMATION_MESSAGE else JOptionPane.ERROR_MESSAGE
                )
                if (result == Result.OK) {
                    joinedLobbies.add(lobby)
                }
            }
        }
        joinPanel.add(connectByIdButton)
        val northPanel = JPanel()
        northPanel.layout = BoxLayout(northPanel, BoxLayout.PAGE_AXIS)
        northPanel.add(createLobby)
        northPanel.add(joinPanel)
        contentPane.add(northPanel, BorderLayout.NORTH)
        val lobbyPanel = JPanel()
        lobbyPanel.layout = BoxLayout(lobbyPanel, BoxLayout.PAGE_AXIS)
        val scrollPane = JScrollPane()
        scrollPane.setViewportView(lobbyPanel)
        contentPane.add(scrollPane, BorderLayout.CENTER)
        setContentPane(contentPane)
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Lobby Manager"
        setBounds(1920 / 2 - 1200 / 2, 1080 / 2 - 600 / 2, 1200, 600)
        updateFuture = executor!!.scheduleAtFixedRate({
            val searchQuery = core!!.lobbyManager().searchQuery
            core!!.lobbyManager().search(searchQuery)
            val publicLobbies =
                core!!.lobbyManager().lobbies
            val lobbies =
                Stream.of(publicLobbies, myLobbies, joinedLobbies)
                    .flatMap { obj: List<Lobby> -> obj.stream() }
                    .filter(distinctByKey { obj: Lobby -> obj.id })
                    .collect(
                        Collectors.toCollection(
                            Supplier { ArrayList() })
                    )
            lobbyPanel.removeAll()
            for (lobby in lobbies) {
                val own = selfUser != null && selfUser!!.userId == lobby.ownerId
                val panel = JPanel()
                panel.layout = BorderLayout()
                val north = JPanel(FlowLayout(FlowLayout.LEADING))
                run {
                    val pub =
                        JRadioButton(lobby.type.name, lobby.type == LobbyType.PUBLIC)
                    pub.isEnabled = false
                    north.add(pub)
                    val id = JLabel(lobby.id.toString())
                    id.font = Font("monospace", Font.PLAIN, 16)
                    id.toolTipText = "Click to copy"
                    id.addMouseListener(object : MouseAdapter() {
                        override fun mouseClicked(e: MouseEvent) {
                            val selection =
                                StringSelection(lobby.id.toString())
                            clipboard.setContents(selection, selection)
                        }
                    })
                    north.add(id)
                }
                panel.add(north, BorderLayout.NORTH)
                val center = JPanel(FlowLayout(FlowLayout.LEADING))
                run {
                    center.add(JLabel("Secret: "))
                    val secret = JLabel(lobby.secret)
                    secret.font = Font("monospace", Font.PLAIN, 16)
                    secret.toolTipText = "Click to copy"
                    secret.addMouseListener(object : MouseAdapter() {
                        override fun mouseClicked(e: MouseEvent) {
                            val selection =
                                StringSelection(lobby.secret)
                            clipboard.setContents(selection, selection)
                        }
                    })
                    center.add(secret)
                    center.add(JSeparator())
                    val memberCount = core!!.lobbyManager().memberCount(lobby)
                    val capacityL =
                        JLabel(memberCount.toString() + "/" + lobby.capacity)
                    center.add(capacityL)
                    center.add(JSeparator())
                    val lockedL = JLabel(if (lobby.isLocked) "Locked" else "Unlocked")
                    center.add(lockedL)
                }
                panel.add(center, BorderLayout.CENTER)
                val east = JPanel()
                run {
                    val metadataButton = JButton("Get metadata")
                    metadataButton.addActionListener { _: ActionEvent? ->
                        val data =
                            core!!.lobbyManager().getLobbyMetadata(lobby).entries.stream()
                                .map { (key, value): Map.Entry<String, String> -> "$key = $value" }
                                .collect(Collectors.joining("\n"))
                        JOptionPane.showMessageDialog(
                            null,
                            data,
                            "Metadata",
                            JOptionPane.PLAIN_MESSAGE
                        )
                    }
                    east.add(metadataButton)
                    val connectButton = JButton("Connect")
                    connectButton.addActionListener { _: ActionEvent? ->
                        core!!.lobbyManager().connectLobby(
                            lobby.id, lobby.secret
                        ) { result: Result, lobby1: Lobby ->
                            JOptionPane.showMessageDialog(
                                null, result.name, "Result",
                                if (result == Result.OK) JOptionPane.INFORMATION_MESSAGE else JOptionPane.ERROR_MESSAGE
                            )
                            if (result == Result.OK) {
                                if (myLobbies.removeIf { l: Lobby -> l.id == lobby.id }) {
                                    myLobbies.add(lobby1)
                                }
                            }
                        }
                    }
                    east.add(connectButton)
                    val disconnectButton = JButton("Disconnect")
                    disconnectButton.addActionListener { _: ActionEvent? ->
                        core!!.lobbyManager().disconnectLobby(
                            lobby.id
                        ) { result: Result ->
                            JOptionPane.showMessageDialog(
                                null, result.name, "Result",
                                if (result == Result.OK) JOptionPane.INFORMATION_MESSAGE else JOptionPane.ERROR_MESSAGE
                            )
                            if (result == Result.OK) {
                                joinedLobbies.removeIf { l: Lobby -> l.id == lobby.id }
                            }
                        }
                    }
                    east.add(disconnectButton)
                    val update = JButton("Update")
                    update.isEnabled = own
                    update.addActionListener { _: ActionEvent? ->
                        val txn =
                            core!!.lobbyManager().getLobbyUpdateTransaction(lobby)
                        txn.setType(type.selectedItem as LobbyType)
                        txn.setCapacity((capacity.value as Int))
                        txn.setLocked(locked.isSelected)
                        core!!.lobbyManager().updateLobby(
                            lobby,
                            txn
                        ) { result: Result ->
                            JOptionPane.showMessageDialog(
                                null, result.name, "Result",
                                if (result == Result.OK) JOptionPane.INFORMATION_MESSAGE else JOptionPane.ERROR_MESSAGE
                            )
                            if (result == Result.OK) {
                                if (myLobbies.removeIf { l: Lobby -> l.id == lobby.id }) {
                                    myLobbies.add(
                                        core!!.lobbyManager().getLobby(lobby.id)
                                    )
                                }
                            }
                        }
                    }
                    east.add(update)
                    val delete = JButton("Delete")
                    delete.isEnabled = own
                    delete.addActionListener { _: ActionEvent? ->
                        core!!.lobbyManager().deleteLobby(
                            lobby
                        ) { result: Result ->
                            JOptionPane.showMessageDialog(
                                null, result.name, "Result",
                                if (result == Result.OK) JOptionPane.INFORMATION_MESSAGE else JOptionPane.ERROR_MESSAGE
                            )
                            myLobbies.removeIf { l: Lobby -> l.id == lobby.id }
                        }
                    }
                    east.add(delete)
                    val setAsActivity = JButton("Set as Activity")
                    setAsActivity.addActionListener { _: ActionEvent? ->
                        activity.details = "Testing lobbies"
                        activity.state = "and having fun!"
                        activity.party().id = lobby.id.toString()
                        activity.party().size().currentSize =
                            core!!.lobbyManager().memberCount(lobby)
                        activity.party().size().maxSize = lobby.capacity
                        activity.secrets().joinSecret =
                            core!!.lobbyManager().getLobbyActivitySecret(lobby)
                        core!!.activityManager().updateActivity(
                            activity
                        ) { result: Result ->
                            JOptionPane.showMessageDialog(
                                null, result.name, "Result",
                                if (result == Result.OK) JOptionPane.INFORMATION_MESSAGE else JOptionPane.ERROR_MESSAGE
                            )
                        }
                    }
                    east.add(setAsActivity)
                }
                panel.add(east, BorderLayout.EAST)
                panel.border = LineBorder(Color.DARK_GRAY)
                lobbyPanel.add(panel)
                panel.maximumSize = Dimension(panel.maximumSize.width, panel.minimumSize.height)
            }
            lobbyPanel.add(Box.createGlue())
            lobbyPanel.revalidate()
        }, 1, 1, TimeUnit.SECONDS)
    }

    companion object {
        private var createParams: CreateParams? = null
        private var core: Core? = null
        private var executor: ScheduledExecutorService? = null
        private var callbackFuture: ScheduledFuture<*>? = null
        private lateinit var updateFuture: ScheduledFuture<*>
        fun <T> distinctByKey(keyExtractor: Function<in T, *>): Predicate<T> {
            val seen: MutableSet<Any> = ConcurrentHashMap.newKeySet()
            return Predicate { t: T -> seen.add(keyExtractor.apply(t)) }
        }

        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val discordLibrary: File = File("/Users/hyunwoo/Documents/Coding_Projects/Jetbrains-Discord-Connect/discord_game_sdk_v2/lib/x86_64/discord_game_sdk.dylib")
            // Initialize the Core
            Core.init(discordLibrary)
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: UnsupportedLookAndFeelException) {
                e.printStackTrace()
            }
            executor = Executors.newSingleThreadScheduledExecutor()
            val frame = Lobby()
            createParams = CreateParams()
            createParams!!.clientID = 1036849907954368563L
            createParams!!.registerEventHandler(frame.eventAdapter)
            core = Core(createParams)
            callbackFuture =
                (executor ?: return).scheduleAtFixedRate(Runnable { core!!.runCallbacks() }, 0, 16, TimeUnit.MILLISECONDS)
            frame.isVisible = true
        }
    }
}