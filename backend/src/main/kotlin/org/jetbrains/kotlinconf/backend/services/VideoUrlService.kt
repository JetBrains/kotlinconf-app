package org.jetbrains.kotlinconf.backend.services

import io.ktor.client.HttpClient
import io.ktor.utils.io.core.Closeable
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.slf4j.LoggerFactory

class VideoUrlService(private val client: HttpClient, config: ConferenceConfig) : Closeable {
    private val log = LoggerFactory.getLogger("VideoUrlService")
    private val repo: String = config.dataRepo
    private val branch: String = config.dataBranch
    private val folder: String = config.videosFolder
    private val updateInterval = config.sessionizeInterval

    private val videoUrls: Map<SessionId, String> = mapOf(
        SessionId("857088") to "https://www.youtube.com/watch?v=F5NaqGF9oT4",
        SessionId("857092") to "https://www.youtube.com/watch?v=E4f5PyNmGLo",
        SessionId("814508") to "https://www.youtube.com/watch?v=NcAW-FZtpzk",
        SessionId("814689") to "https://www.youtube.com/watch?v=WTWW7OOHQIw",
        SessionId("793515") to "https://www.youtube.com/watch?v=5lkZj4v4-ks",
        SessionId("793888") to "https://www.youtube.com/watch?v=mQhtLCMiUMA",
        SessionId("795861") to "https://www.youtube.com/watch?v=bkd6EAPIVe0",
        SessionId("786515") to "https://www.youtube.com/watch?v=kIEBQ_czdxs",
        SessionId("762779") to "https://www.youtube.com/watch?v=IUrA3mDSWZQ",
        SessionId("794504") to "https://www.youtube.com/watch?v=Hy5TOyDDics",
        SessionId("795179") to "https://www.youtube.com/watch?v=QExksqeNWbY",
        SessionId("800713") to "https://www.youtube.com/watch?v=9KdP2idt6LE",
        SessionId("867231") to "https://www.youtube.com/watch?v=D3hCBrxJHLo",
        SessionId("812350") to "https://www.youtube.com/watch?v=434WFSiYj9k",
        SessionId("792910") to "https://www.youtube.com/watch?v=zBgb0z1pQkM",
        SessionId("795318") to "https://www.youtube.com/watch?v=ufQvrZmQw_I",
        SessionId("793244") to "https://www.youtube.com/watch?v=qpM3_ymNkP8",
        SessionId("779171") to "https://www.youtube.com/watch?v=JKLqQiYh8GQ",
        SessionId("793302") to "https://www.youtube.com/watch?v=z-u99yZFn5o",
        SessionId("796310") to "https://www.youtube.com/watch?v=M9Nni0Qywyo",
        SessionId("800297") to "https://www.youtube.com/watch?v=YKTlW8Qkj0w",
        SessionId("774874") to "https://www.youtube.com/watch?v=XcxqhmG_bdg",
        SessionId("795195") to "https://www.youtube.com/watch?v=bKwdgMKyFOY",
        SessionId("816767") to "https://www.youtube.com/watch?v=a8uMQk4R6jQ",
        SessionId("779829") to "https://www.youtube.com/watch?v=Q4oj9fkaDCs",
        SessionId("836755") to "https://www.youtube.com/watch?v=Zw7eJFciwXg",
        SessionId("856501") to "https://www.youtube.com/watch?v=O8WQCrdza8E",
        SessionId("795897") to "https://www.youtube.com/watch?v=PW_qc6EUBQE",
        SessionId("794336") to "https://www.youtube.com/watch?v=fv-vw1huJ-4",
        SessionId("783886") to "https://www.youtube.com/watch?v=UebhKUdO2sQ",
        SessionId("793235") to "https://www.youtube.com/watch?v=3vaAVtnrzAM",
        SessionId("885501") to "https://www.youtube.com/watch?v=JxTIZAEos8Y",
        SessionId("793709") to "https://www.youtube.com/watch?v=vewgb-vyJME",
        SessionId("795839") to "https://www.youtube.com/watch?v=vMNCAryfJys",
        SessionId("800068") to "https://www.youtube.com/watch?v=57Ed70ROmm4",
        SessionId("795976") to "https://www.youtube.com/watch?v=hxEM5J6QfLY",
        SessionId("812314") to "https://www.youtube.com/watch?v=2Vp2QeBZkfo",
        SessionId("812209") to "https://www.youtube.com/watch?v=ojuBhKRzyL8",
        SessionId("796169") to "https://www.youtube.com/watch?v=FXGT6HbBXNw",
        SessionId("779345") to "https://www.youtube.com/watch?v=4A6aLK2KznU",
        SessionId("858141") to "https://www.youtube.com/watch?v=5Sc3Qdb0XoQ",
        SessionId("781372") to "https://www.youtube.com/watch?v=9P7qUGi5_gc",
        SessionId("797367") to "https://www.youtube.com/watch?v=dcgwPpVT74g",
        SessionId("795941") to "https://www.youtube.com/watch?v=sDA28kH6AIc",
        SessionId("795620") to "https://www.youtube.com/watch?v=n0LpCCv3VEY",
        SessionId("788999") to "https://www.youtube.com/watch?v=qJB5iX2cOu0",
        SessionId("799261") to "https://www.youtube.com/watch?v=QTX5_JV4TVU",
        SessionId("779451") to "https://www.youtube.com/watch?v=HSIhkB5bGJs",
        SessionId("759131") to "https://www.youtube.com/watch?v=OuX5325yq_I",
        SessionId("788419") to "https://www.youtube.com/watch?v=P0mo8Tzrapo",
        SessionId("774286") to "https://www.youtube.com/watch?v=vWIDRH6aQfI",
        SessionId("812400") to "https://www.youtube.com/watch?v=RJtiFt5pbfs",
        SessionId("794830") to "https://www.youtube.com/watch?v=xgfeqj8UyVA",
        SessionId("795546") to "https://www.youtube.com/watch?v=QSoG8OaCSgw",
        SessionId("857571") to "https://www.youtube.com/watch?v=bC_grxuSO08",
        SessionId("811578") to "https://www.youtube.com/watch?v=clDGqPfaIto",
        SessionId("795023") to "https://www.youtube.com/watch?v=lcqtRQ5Fou8",
        SessionId("787545") to "https://www.youtube.com/watch?v=OyEfB6Q4Y0s",
        SessionId("795778") to "https://www.youtube.com/watch?v=2Eyq1VhBxUg",
        SessionId("856531") to "https://www.youtube.com/watch?v=-CznWwKD-WE",
        SessionId("793518") to "https://www.youtube.com/watch?v=z_IO2_XeW2Q",
        SessionId("811915") to "https://www.youtube.com/watch?v=K2PN03AepC0",
        SessionId("796359") to "https://www.youtube.com/watch?v=n6egwuOnGuk",
        SessionId("786540") to "https://www.youtube.com/watch?v=AlGWsTXnWsY",
        SessionId("798313") to "https://www.youtube.com/watch?v=KEsVNrzPf24",
        SessionId("792693") to "https://www.youtube.com/watch?v=l7alNC819MU",
        SessionId("800400") to "https://www.youtube.com/watch?v=rKbM3e0OidI",
        SessionId("784566") to "https://www.youtube.com/watch?v=O0BqoLcRuJI",
        SessionId("796464") to "https://www.youtube.com/watch?v=eryPIdJjBgk",
        SessionId("811421") to "https://www.youtube.com/watch?v=jDz_yNZkEzk",
        SessionId("774210") to "https://www.youtube.com/watch?v=6jZa4B-If-I",
        SessionId("904371") to "https://www.youtube.com/watch?v=Jyj4kdK8a6o",
        SessionId("791121") to "https://www.youtube.com/watch?v=SwNTpgp262o",
        SessionId("787342") to "https://www.youtube.com/watch?v=uGgK0F3cLEI",
        SessionId("792742") to "https://www.youtube.com/watch?v=PY7eOmnOK3s",
        SessionId("782203") to "https://www.youtube.com/watch?v=oRKytkr2FOY",
    )

    suspend fun getVideoUrls(): Map<SessionId, String> = videoUrls

    override fun close() {
    }
}
