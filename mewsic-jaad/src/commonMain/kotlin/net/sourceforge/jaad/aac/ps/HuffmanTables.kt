package net.sourceforge.jaad.aac.ps

internal interface HuffmanTables {
    companion object {
        /* binary lookup huffman tables */
        val f_huff_iid_def = arrayOf(
            intArrayOf( /*0*/-31, 1),
            intArrayOf(2, 3),
            intArrayOf( /*1*/-30,  /*-1*/-32),
            intArrayOf(4, 5),
            intArrayOf( /*2*/-29,  /*-2*/-33),
            intArrayOf(6, 7),
            intArrayOf( /*3*/-28,  /*-3*/-34),
            intArrayOf(8, 9),
            intArrayOf( /*-4*/-35,  /*4*/-27),
            intArrayOf( /*5*/-26, 10),
            intArrayOf( /*-5*/-36, 11),
            intArrayOf( /*6*/-25, 12),
            intArrayOf( /*-6*/-37, 13),
            intArrayOf( /*-7*/-38, 14),
            intArrayOf( /*7*/-24, 15),
            intArrayOf(16, 17),
            intArrayOf( /*8*/-23,  /*-8*/-39),
            intArrayOf(18, 19),
            intArrayOf( /*9*/-22,  /*10*/-21),
            intArrayOf(20, 21),
            intArrayOf( /*-9*/-40,  /*11*/-20),
            intArrayOf(22, 23),
            intArrayOf( /*-10*/-41, 24),
            intArrayOf(25, 26),
            intArrayOf( /*-11*/-42,  /*-14*/-45),
            intArrayOf( /*-13*/-44,  /*-12*/-43),
            intArrayOf( /*12*/-19, 27),
            intArrayOf( /*13*/-18,  /*14*/-17)
        )
        val t_huff_iid_def = arrayOf(
            intArrayOf( /*0*/-31, 1),
            intArrayOf( /*-1*/-32, 2),
            intArrayOf( /*1*/-30, 3),
            intArrayOf( /*-2*/-33, 4),
            intArrayOf( /*2*/-29, 5),
            intArrayOf( /*-3*/-34, 6),
            intArrayOf( /*3*/-28, 7),
            intArrayOf( /*-4*/-35, 8),
            intArrayOf( /*4*/-27, 9),
            intArrayOf( /*-5*/-36, 10),
            intArrayOf( /*5*/-26, 11),
            intArrayOf( /*-6*/-37, 12),
            intArrayOf( /*6*/-25, 13),
            intArrayOf( /*7*/-24, 14),
            intArrayOf( /*-7*/-38, 15),
            intArrayOf(16, 17),
            intArrayOf( /*8*/-23,  /*-8*/-39),
            intArrayOf(18, 19),
            intArrayOf(20, 21),
            intArrayOf(22, 23),
            intArrayOf( /*9*/-22,  /*-14*/-45),
            intArrayOf( /*-13*/-44,  /*-12*/-43),
            intArrayOf(24, 25),
            intArrayOf(26, 27),
            intArrayOf( /*-11*/-42,  /*-10*/-41),
            intArrayOf( /*-9*/-40,  /*10*/-21),
            intArrayOf( /*11*/-20,  /*12*/-19),
            intArrayOf( /*13*/-18,  /*14*/-17)
        )
        val f_huff_iid_fine = arrayOf(
            intArrayOf(1,  /*0*/-31),
            intArrayOf(2, 3),
            intArrayOf(4,  /*-1*/-32),
            intArrayOf( /*1*/-30, 5),
            intArrayOf( /*-2*/-33,  /*2*/-29),
            intArrayOf(6, 7),
            intArrayOf( /*-3*/-34,  /*3*/-28),
            intArrayOf(8, 9),
            intArrayOf( /*-4*/-35,  /*4*/-27),
            intArrayOf(10, 11),
            intArrayOf( /*-5*/-36,  /*5*/-26),
            intArrayOf(12, 13),
            intArrayOf( /*-6*/-37,  /*6*/-25),
            intArrayOf(14, 15),
            intArrayOf( /*7*/-24, 16),
            intArrayOf(17, 18),
            intArrayOf(19,  /*-8*/-39),
            intArrayOf( /*8*/-23, 20),
            intArrayOf(21,  /*-7*/-38),
            intArrayOf( /*10*/-21, 22),
            intArrayOf(23,  /*-9*/-40),
            intArrayOf( /*9*/-22, 24),
            intArrayOf( /*-11*/-42,  /*11*/-20),
            intArrayOf(25, 26),
            intArrayOf(27,  /*-10*/-41),
            intArrayOf(28,  /*-12*/-43),
            intArrayOf( /*12*/-19, 29),
            intArrayOf(30, 31),
            intArrayOf(32,  /*-14*/-45),
            intArrayOf( /*14*/-17, 33),
            intArrayOf(34,  /*-13*/-44),
            intArrayOf( /*13*/-18, 35),
            intArrayOf(36, 37),
            intArrayOf(38,  /*-15*/-46),
            intArrayOf( /*15*/-16, 39),
            intArrayOf(40, 41),
            intArrayOf(42, 43),
            intArrayOf( /*-17*/-48,  /*17*/-14),
            intArrayOf(44, 45),
            intArrayOf(46, 47),
            intArrayOf(48, 49),
            intArrayOf( /*-16*/-47,  /*16*/-15),
            intArrayOf( /*-21*/-52,  /*21*/-10),
            intArrayOf( /*-19*/-50,  /*19*/-12),
            intArrayOf( /*-18*/-49,  /*18*/-13),
            intArrayOf(50, 51),
            intArrayOf(52, 53),
            intArrayOf(54, 55),
            intArrayOf(56, 57),
            intArrayOf(58, 59),
            intArrayOf( /*-26*/-57,  /*-25*/-56),
            intArrayOf( /*-28*/-59,  /*-27*/-58),
            intArrayOf( /*-22*/-53,  /*22*/-9),
            intArrayOf( /*-24*/-55,  /*-23*/-54),
            intArrayOf( /*25*/-6,  /*26*/-5),
            intArrayOf( /*23*/-8,  /*24*/-7),
            intArrayOf( /*29*/-2,  /*30*/-1),
            intArrayOf( /*27*/-4,  /*28*/-3),
            intArrayOf( /*-30*/-61,  /*-29*/-60),
            intArrayOf( /*-20*/-51,  /*20*/-11)
        )
        val t_huff_iid_fine = arrayOf(
            intArrayOf(1,  /*0*/-31),
            intArrayOf( /*1*/-30, 2),
            intArrayOf(3,  /*-1*/-32),
            intArrayOf(4, 5),
            intArrayOf(6, 7),
            intArrayOf( /*-2*/-33,  /*2*/-29),
            intArrayOf(8,  /*-3*/-34),
            intArrayOf( /*3*/-28, 9),
            intArrayOf( /*-4*/-35,  /*4*/-27),
            intArrayOf(10, 11),
            intArrayOf( /*5*/-26, 12),
            intArrayOf(13, 14),
            intArrayOf( /*-6*/-37,  /*6*/-25),
            intArrayOf(15, 16),
            intArrayOf(17,  /*-5*/-36),
            intArrayOf(18,  /*-7*/-38),
            intArrayOf( /*7*/-24, 19),
            intArrayOf(20, 21),
            intArrayOf( /*9*/-22, 22),
            intArrayOf(23, 24),
            intArrayOf( /*-8*/-39,  /*8*/-23),
            intArrayOf(25, 26),
            intArrayOf( /*11*/-20, 27),
            intArrayOf(28, 29),
            intArrayOf( /*-10*/-41,  /*10*/-21),
            intArrayOf(30, 31),
            intArrayOf(32,  /*-9*/-40),
            intArrayOf(33,  /*-13*/-44),
            intArrayOf( /*13*/-18, 34),
            intArrayOf(35, 36),
            intArrayOf(37,  /*-12*/-43),
            intArrayOf( /*12*/-19, 38),
            intArrayOf(39,  /*-11*/-42),
            intArrayOf(40, 41),
            intArrayOf(42, 43),
            intArrayOf(44, 45),
            intArrayOf(46,  /*-15*/-46),
            intArrayOf( /*15*/-16, 47),
            intArrayOf( /*-14*/-45,  /*14*/-17),
            intArrayOf(48, 49),
            intArrayOf( /*-21*/-52,  /*-20*/-51),
            intArrayOf( /*18*/-13,  /*19*/-12),
            intArrayOf( /*-19*/-50,  /*-18*/-49),
            intArrayOf(50, 51),
            intArrayOf(52, 53),
            intArrayOf(54, 55),
            intArrayOf(56,  /*-17*/-48),
            intArrayOf( /*17*/-14, 57),
            intArrayOf(58,  /*-16*/-47),
            intArrayOf( /*16*/-15, 59),
            intArrayOf( /*-26*/-57,  /*26*/-5),
            intArrayOf( /*-28*/-59,  /*-27*/-58),
            intArrayOf( /*29*/-2,  /*30*/-1),
            intArrayOf( /*27*/-4,  /*28*/-3),
            intArrayOf( /*-30*/-61,  /*-29*/-60),
            intArrayOf( /*-25*/-56,  /*25*/-6),
            intArrayOf( /*-24*/-55,  /*24*/-7),
            intArrayOf( /*-23*/-54,  /*23*/-8),
            intArrayOf( /*-22*/-53,  /*22*/-9),
            intArrayOf( /*20*/-11,  /*21*/-10)
        )
        val f_huff_icc = arrayOf(
            intArrayOf( /*0*/-31, 1),
            intArrayOf( /*1*/-30, 2),
            intArrayOf( /*-1*/-32, 3),
            intArrayOf( /*2*/-29, 4),
            intArrayOf( /*-2*/-33, 5),
            intArrayOf( /*3*/-28, 6),
            intArrayOf( /*-3*/-34, 7),
            intArrayOf( /*4*/-27, 8),
            intArrayOf( /*5*/-26, 9),
            intArrayOf( /*-4*/-35, 10),
            intArrayOf( /*6*/-25, 11),
            intArrayOf( /*-5*/-36, 12),
            intArrayOf( /*7*/-24, 13),
            intArrayOf( /*-6*/-37,  /*-7*/-38)
        )
        val t_huff_icc = arrayOf(
            intArrayOf( /*0*/-31, 1),
            intArrayOf( /*1*/-30, 2),
            intArrayOf( /*-1*/-32, 3),
            intArrayOf( /*2*/-29, 4),
            intArrayOf( /*-2*/-33, 5),
            intArrayOf( /*3*/-28, 6),
            intArrayOf( /*-3*/-34, 7),
            intArrayOf( /*4*/-27, 8),
            intArrayOf( /*-4*/-35, 9),
            intArrayOf( /*5*/-26, 10),
            intArrayOf( /*-5*/-36, 11),
            intArrayOf( /*6*/-25, 12),
            intArrayOf( /*-6*/-37, 13),
            intArrayOf( /*-7*/-38,  /*7*/-24)
        )
        val f_huff_ipd = arrayOf(
            intArrayOf(1,  /*0*/-31),
            intArrayOf(2, 3),
            intArrayOf( /*1*/-30, 4),
            intArrayOf(5, 6),
            intArrayOf( /*4*/-27,  /*5*/-26),
            intArrayOf( /*3*/-28,  /*6*/-25),
            intArrayOf( /*2*/-29,  /*7*/-24)
        )
        val t_huff_ipd = arrayOf(
            intArrayOf(1,  /*0*/-31),
            intArrayOf(2, 3),
            intArrayOf(4, 5),
            intArrayOf( /*1*/-30,  /*7*/-24),
            intArrayOf( /*5*/-26, 6),
            intArrayOf( /*2*/-29,  /*6*/-25),
            intArrayOf( /*4*/-27,  /*3*/-28)
        )
        val f_huff_opd = arrayOf(
            intArrayOf(1,  /*0*/-31),
            intArrayOf(2, 3),
            intArrayOf( /*7*/-24,  /*1*/-30),
            intArrayOf(4, 5),
            intArrayOf( /*3*/-28,  /*6*/-25),
            intArrayOf( /*2*/-29, 6),
            intArrayOf( /*5*/-26,  /*4*/-27)
        )
        val t_huff_opd = arrayOf(
            intArrayOf(1,  /*0*/-31),
            intArrayOf(2, 3),
            intArrayOf(4, 5),
            intArrayOf( /*1*/-30,  /*7*/-24),
            intArrayOf( /*5*/-26,  /*2*/-29),
            intArrayOf( /*6*/-25, 6),
            intArrayOf( /*4*/-27,  /*3*/-28)
        )
    }
}